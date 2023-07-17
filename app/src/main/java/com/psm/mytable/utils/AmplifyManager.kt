import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.S3ClientOptions

class AmplifyManager(
    private val context: Context
){

    private val identityPoolId = "us-east-2:983cea4f-ee6a-4f5a-8b0c-210df4950bb2"
    val s3Client: AmazonS3
    private val credentialsProvider = CognitoCachingCredentialsProvider(context, identityPoolId, Regions.US_EAST_2)

    init{
        s3Client = AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_SOUTHEAST_2)).apply{
            setS3ClientOptions(
                S3ClientOptions.builder()
                    .setAccelerateModeEnabled(true)
                    .build()
            )
        }
    }

}