package com.matosic.Facebook.service;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfService {

	private String minioUrl = "http://localhost:9000";

	private String accessKey = "minioadmin";
	//
//	    @Value("${minio.secretKey}")
	private String secretKey = "minioadmin";

	private String bucketName = "facebook";

	private final MinioClient minioClient;
	private final RestHighLevelClient elasticsearchClient;

	public PdfService(RestHighLevelClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
		this.minioClient = MinioClient.builder().endpoint(minioUrl).credentials(accessKey, secretKey).build();
	}

	public void indexPdfContent(String fileName) throws Exception {
		try (InputStream inputStream = minioClient
				.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build())) {
			PDDocument document = PDDocument.load(inputStream);
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);

			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("fileName", fileName);
			jsonMap.put("content", text);

			IndexRequest indexRequest = new IndexRequest("pdf-index").source(jsonMap,
					org.elasticsearch.xcontent.XContentType.JSON);

			elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
			document.close();
		}
	}
}
