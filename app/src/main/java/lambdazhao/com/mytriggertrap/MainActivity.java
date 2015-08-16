package lambdazhao.com.mytriggertrap;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    Button btnShot=null;
    EditText txtShutterSpeed=null;
    Thread th=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnShot=(Button)findViewById(R.id.btnShot);
        txtShutterSpeed=(EditText)findViewById(R.id.txtShutterSpeed);

        btnShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final double shutterSpeed=Double.parseDouble(txtShutterSpeed.getText().toString());
                if(th!=null && th.getState()!= Thread.State.TERMINATED)
                    return;
                th=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int sampleFrequency=48000;
                        int frequency=17000;
                        int bufferSize= AudioTrack.getMinBufferSize(sampleFrequency, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

                        short[] audiodata=new short[bufferSize*5];
                        //short[] audiodata=new short[48000*2];

                        try {
                            //DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(recordingFile)));
                            AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,sampleFrequency,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT,audiodata.length,AudioTrack.MODE_STREAM);
                            //audioTrack.setVolume(AudioTrack.getMaxVolume());
                            audioTrack.play();
                            Log.d("wt", "start");
                            int cs=0;
                            while (true) {

                                int es=(int)(shutterSpeed*sampleFrequency);
                                for(int i=0;i<audiodata.length;i++){

                                    if(cs<es)
                                        audiodata[i]=(short)(Short.MAX_VALUE *Math.sin((double)cs*frequency/sampleFrequency*2*Math.PI));
                                    else
                                        audiodata[i]=0;
                                    i++;
                                    cs++;
                                }

                                audioTrack.write(audiodata, 0, audiodata.length);
                                if(cs>=es)
                                    break;
                            }

                            Log.d("wt", "end gen");

                            Thread.sleep(audiodata.length/sampleFrequency*1000+100);
                            audioTrack.stop();
                            audioTrack.release();
                            Log.d("wt","done");
                        }
                        catch (  Throwable t) {
                            Log.e("AudioTrack", "Playback Failed");
                        }



                    }
                });
                th.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
