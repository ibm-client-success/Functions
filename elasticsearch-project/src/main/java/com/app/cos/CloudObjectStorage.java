package com.app.cos;

import java.io.File;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;

public class CloudObjectStorage {
	
	private static String COS_ENDPOINT = "s3-api.us-geo.objectstorage.softlayer.net";
	private static String COS_API_KEY_ID = "<COS_API_KEY>";
	private static String COS_AUTH_ENDPOINT = "https://iam.ng.bluemix.net/oidc/token";
	private static String COS_SERVICE_CRN = "<COS_SERVICE_CRN>";
	private static String COS_BUCKET_LOCATION = "us-geo";
	
	public AmazonS3 createClient() {
	    SDKGlobalConfiguration.IAM_ENDPOINT = COS_AUTH_ENDPOINT;

	    AmazonS3 _cos = null;
	    
	    try {
	    	_cos = createClient(COS_API_KEY_ID, COS_SERVICE_CRN, COS_ENDPOINT, COS_BUCKET_LOCATION);
	    } catch (SdkClientException sdke) {
	        System.out.printf("SDK Error: %s\n", sdke.getMessage());
	    } catch (Exception e) {
	        System.out.printf("Error: %s\n", e.getMessage());
	    }
	    
	    return _cos;
	}
	
	public static AmazonS3 createClient(String api_key, String service_instance_id, String endpoint_url, String location) {
	    AWSCredentials credentials;
	    if (endpoint_url.contains("objectstorage.softlayer.net")) {
	        credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);
	    } else {
	        String access_key = api_key;
	        String secret_key = service_instance_id;
	        credentials = new BasicAWSCredentials(access_key, secret_key);
	    }

	    ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(10000);
	    clientConfig.setUseTcpKeepAlive(true);

	    AmazonS3 cos = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
	            .withEndpointConfiguration(new EndpointConfiguration(endpoint_url, location)).withPathStyleAccessEnabled(true)
	            .withClientConfiguration(clientConfig).build();

	    return cos;
	}
}
