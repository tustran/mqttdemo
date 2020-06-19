//https://www.youtube.com/watch?v=gae05UKmXKc
//https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/#publisher

//https://www.youtube.com/watch?v=BAkGm02WBc0
//https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service/
package calculator.bk191.com.cloudmqttdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    String dataSendmqtt="";
    TextView dataReceived;
    TextView dataSend;
    Switch switchID;
    TextView sensorID;
    TextView sensorMois;
    TextView sensorTemp;
    int counter = 0;

    GraphView graphTemperature, graphHumidity;

    private void initGraphs() {
        graphTemperature = findViewById(R.id.graphTemperature);
        graphHumidity = findViewById(R.id.graphHumidity);

        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(100);
        graphTemperature.getViewport().setYAxisBoundsManual(true);

        graphHumidity.getViewport().setMinY(0);
        graphHumidity.getViewport().setMaxY(100);
        graphHumidity.getViewport().setYAxisBoundsManual(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataReceived = (TextView) findViewById(R.id.dataReceive);
        dataSend = (TextView) findViewById(R.id.dataSend);
        sensorID = (TextView) findViewById(R.id.idsensor);
        sensorMois = (TextView) findViewById(R.id.sensormois);
        sensorTemp = (TextView) findViewById(R.id.sensortemp);
        startMqtt();
        switchID=((Switch) findViewById(R.id.switch_id));
        switchID.setChecked(false);
        switchID.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
//                        Toast.makeText(AreaFragment.super.getContext(),"Switch is " + (motor.status ? "On" : "Off"),Toast.LENGTH_LONG).show();
                        if (b){
                            sendDataToMQTT("Speaker","1","10");
                        }
                        else{
                            sendDataToMQTT("Speaker","0","0");
                        };
                    }
                });
        counter = 0;
//        sendDataToMQTT();
    }

//    private void sendDataToMQTT(){
//        final Timer aTimer = new Timer();
//        TimerTask aTask = new TimerTask() {
//            @Override
//            public void run() {
//                MqttMessage msg = new MqttMessage();
//                msg.setId(1234);
//                msg.setQos(0);
//                msg.setRetained(true);
//                counter++;
//                String data = "ADC: " + counter +"";
//                byte[] b = data.getBytes(Charset.forName("UTF-8"));
//                //msg.setPayload(new byte[]{'4','5'});
//                msg.setPayload(b);
//                try {
//                    mqttHelper.mqttAndroidClient.publish("sensor/abc", msg);
//
//                }catch (MqttException e){
//
//                }
//
//            }
//        };
//        aTimer.schedule(aTask, 10000, 10000);
//    }

    public void sendDataToMQTT(final String ID, final String value1, final String value2){
        final Timer aTimer = new Timer();
        TimerTask aTask = new TimerTask() {
            @Override
            public void run() {
                MqttMessage msg = new MqttMessage();
                msg.setId(1234);
                msg.setQos(0);
                msg.setRetained(true);

                //String data = ID + ":[" + value1 + "," + value2 + "]";
                String data = "[{\"device_id\":\"Speaker\", \"values\":[\"" + value1 + "\",\"" + value2 + "\"]}]";
                byte[] b = data.getBytes(Charset.forName("UTF-8"));
                msg.setPayload(b);

                try {
                    mqttHelper.mqttAndroidClient.publish("Topic/Speaker", msg);
                    Log.e("publish","published");
//                    dataSendmqtt=new String(data);
//                    dataSend.setText("Send: ".concat(data));

                }catch (MqttException e){
                }
            }
        };
        aTimer.schedule(aTask,1);
//        aTimer.schedule(aTask, 10000, 10000);
    }

    private void startMqtt(){
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());

                JSONArray obj = new JSONArray(mqttMessage.toString());
                Log.w("Debug",obj.toString());
                JSONObject objarr = obj.getJSONObject(0);
                Log.w("Debug",objarr.toString());
                JSONArray arr = objarr.getJSONArray("values");
                Log.w("Debug",arr.toString());
//                Log.w("Debug",arr.getJSONObject(0).toString());
                sensorID.setText(objarr.getString("device_id"));
                sensorTemp.setText(arr.getString(0)+"oC");
                sensorMois.setText(arr.getString(1)+"%");
                dataReceived.setText("Receive: " + mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
