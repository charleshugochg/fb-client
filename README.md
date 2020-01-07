# FBClient
Skrape facebook video url using fbdown.net

## Basic Usage
```
import master.charleshugo.fbclient.FBClient;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
        new FBClient().setOnResponseListener(new FBClient.OnResponseListener(){

            @Override
            public void onResponse(String hdlink, String sdlink)
            {
              // TODO: Implement this method

            }

        }).post("your id");
    }
}
```
