
import java.io.InputStream;
import java.io.FileInputStream;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.app.cos.*;
import com.app.trigger.*;

    /**
     * Copies a file to COS and trigger a sequence of events in IBM Cloud
     *
     */
    public class App
    {
        public static void main( String[] args ) throws Exception
        {
            CloudObjectStorage cos = new CloudObjectStorage();
            EventTrigger et = new EventTrigger();


            String encodedDocString = null;
            String docName = null;

            for (String s: args) {
                docName = s;
            }

            System.out.println("Document Name: " + docName);

            if (!docName.isEmpty()) {
                InputStream is = new FileInputStream(docName);
                System.out.println("Creating S3 Client.");
                AmazonS3 s3 = cos.createClient();

                System.out.println("Uploading " + docName + " to Cloud Object Storage.");
                s3.putObject("BUCKET_NAME", docName, is, null);

                System.out.println("Triggering IBM Functions sequence.");
                et.triggerSequence(docName);


            } else {
                System.out.println("No file name given.");
            }
        }
    }
