package com.example.bluetoothchat;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class BluetoothChatting extends AppCompatActivity {
    private  NotificationManager notificationManager;
    private TextView status;
    private  BluetoothAdapter bluetoothAdapter;
    private EditText edit;

    public static Boolean mute=false;
    private  String device_name;
    private String SecondMsg="";
    private BluetoothDevice bluetoothDevice;
    private String ThirdMsg="";
    private String FourthMsg="";
    private String FivthMsg="";
    private SharedPreferences sharedPreferences;
    private SpannableString[] spanArray;
    private Boolean ScreenOn=false;
    SendReceive sendReceive;
    private int FiveNotify=1,count=0;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    static final int STATE_WAITING=6;
    private Boolean p=false;
    private  String[] storeMsg;
    private LinearLayout item;
    private final List<BluetoothGetSet> messagesList = new ArrayList<>();
    private BlueAdapter blueAdapter;
    private RecyclerView userMessagesList;
    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    private final Handler handler=new Handler(new Handler.Callback() {
        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_WAITING:
                    status.setText("Waiting to be Connected");
                    break;

                case STATE_MESSAGE_RECEIVED:
                    final MediaPlayer mp = MediaPlayer.create(BluetoothChatting.this, R.raw.notifiy);
                    Date currentTime = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    String formattedTime = sdf.format(currentTime);
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);


                    if(ScreenOn) {
                        showNotification(device_name, tempMsg);
                    }
                    else mp.start();
                    BluetoothGetSet bluetoothObj = new BluetoothGetSet();
                    bluetoothObj.setFrom("psf");
                    bluetoothObj.setMessage(tempMsg);
                    bluetoothObj.setTime(formattedTime);
                    messagesList.add(bluetoothObj);
                    blueAdapter.notifyDataSetChanged();
                    userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                    break;
            }
            return false;
        }
    });

    private void showNotification(String from,String Messages) {
        //String hiText = "<font color='#FF0000'>hi</font>";
       // Html.fromHtml(hiText)

        count+=1;
        if(FiveNotify>=1){
            SecondMsg = storeMsg[0];
            storeMsg[0]=" "+device_name+" "+Messages;
            spanArray[0]=span(storeMsg[0]);
        }
        if(FiveNotify>=2){
            ThirdMsg=storeMsg[1];
            storeMsg[1]=SecondMsg;
            spanArray[1]=span(storeMsg[1]);
        }
        if(FiveNotify>=3){
            FourthMsg=storeMsg[2];
            storeMsg[2]=ThirdMsg;
            spanArray[2]=span(storeMsg[2]);

        }
        if(FiveNotify>=4){
            FivthMsg=storeMsg[3];
            storeMsg[3]=FourthMsg;
            spanArray[3]=span(storeMsg[3]);
        }
        if(FiveNotify>=5){
            storeMsg[4]=FivthMsg;
            spanArray[4]=span(storeMsg[4]);
        }
        if(FiveNotify<5) FiveNotify+=1;

        if(sharedPreferences.getBoolean("notificationSound",false)&&!mute) {
            Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vi.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                //noinspection deprecation
                vi.vibrate(200);
            }
        }


        Intent intent = new Intent(this, BluetoothChatting.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder;
        if(sharedPreferences.getBoolean("notificationHide",false)&&!mute) {


            Intent buttonIntent = new Intent(this, NotificationReceiver.class);
            buttonIntent.putExtra("broad", "BLUE SATTAI");

            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent buttonPendingIntent = PendingIntent.getBroadcast(BluetoothChatting.this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                    R.color.red, "Mute", buttonPendingIntent)
                    .build();


            builder = new NotificationCompat.Builder(this, "Notification")

                    .setContentTitle("\n")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.circle)
                    .addAction(action)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);





            builder.setContentText(span(" " + device_name + " " + Messages));

            builder.setStyle(new NotificationCompat.InboxStyle()

                    .addLine(spanArray[4])
                    .addLine(spanArray[3])
                    .addLine(spanArray[2])
                    .addLine(spanArray[1])
                    .addLine(spanArray[0])
            );
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BluetoothChatting.this);

            notificationManager.notify(1, builder.build());

        }
        else if(!mute){
            builder = new NotificationCompat.Builder(this, "Notification")

                    .setContentTitle("bluetoothChat")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.circle)
                    .setContentText(count+" new notification")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
             NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BluetoothChatting.this);

            notificationManager.notify(1, builder.build());

        }





    }

    private SpannableString span(String msg) {
        SpannableString spannableString = new SpannableString(msg);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
        spannableString.setSpan(colorSpan,1,device_name.length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spanArray=new SpannableString[5];
        storeMsg = new String[5];

        sharedPreferences=getSharedPreferences("MyPreferences", MODE_PRIVATE);

        setContentView(R.layout.activity_bluetooth_chatting);
        Intent i = getIntent();
        device_name = i.getStringExtra("device_name");
        String device_address = i.getStringExtra("device_address");
        bluetoothDevice = getIntent().getExtras().getParcelable("option");
        TextView text = findViewById(R.id.bluetooth_textView7);
        Button connect = findViewById(R.id.connect);
        Button disconnect = findViewById(R.id.disconnect);
        status=findViewById(R.id.bluetooth_textView2);
        ImageButton option = findViewById(R.id.option);
        RelativeLayout topLay = findViewById(R.id.bluetooth_rel);
        ImageView send = findViewById(R.id.bluetooth_send);
        item=findViewById(R.id.item);
        if(device_name.length()>10) {
            text.setText(device_name.substring(0, 9));
        }
        else{ text.setText(device_name);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1=new NotificationChannel("Notification","Notification", NotificationManager.IMPORTANCE_HIGH);
            setNotify(channel1);
        }

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        edit=findViewById(R.id.bluetooth_input_message);

        blueAdapter = new BlueAdapter(messagesList);
        edit=findViewById(R.id.bluetooth_input_message);
        userMessagesList= findViewById(R.id.bluetooth_cycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(blueAdapter);



        ServerClass serverClass=new ServerClass(true);
        serverClass.start();

        ClientClass clientClass = new ClientClass(bluetoothDevice);
        clientClass.start();

        option.setOnClickListener(v -> {

           if(p) {item.setVisibility(View.GONE); p=false;}
           else {item.setVisibility(View.VISIBLE); p=true;}

        });

        topLay.setOnClickListener(v -> item.setVisibility(View.GONE));
        send.setOnClickListener(v -> {
            String alpha=edit.getText().toString();
            if(alpha.length()!=0) {
                Date currentTime = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String formattedTime = sdf.format(currentTime);
                String string = String.valueOf(edit.getText());
                sendReceive.write(string.getBytes());
                BluetoothGetSet bluetoothObj = new BluetoothGetSet();
                bluetoothObj.setFrom("sender");
                bluetoothObj.setMessage(alpha);
                bluetoothObj.setTime(formattedTime);

                messagesList.add(bluetoothObj);
                blueAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                edit.setText("");

            }

        });

        connect.setOnClickListener(v -> {
            ClientClass clientClass1 =new ClientClass(bluetoothDevice);
            clientClass1.start();
        });
        disconnect.setOnClickListener(v -> {

        });
    }

    private void setNotify(NotificationChannel ch) {
         notificationManager= getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(ch);
    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        @SuppressLint("MissingPermission")
        public ServerClass(boolean x) {

            if (x) {Toast.makeText(BluetoothChatting.this, "Disconnected", Toast.LENGTH_SHORT).show();}
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (true)
            {

                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket=serverSocket.accept();

                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_WAITING;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive=new SendReceive(socket);
                    sendReceive.start();


                    break;
                }

            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientClass (BluetoothDevice device1)
        {

            try {
                socket= device1.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run()
        {

            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive=new SendReceive(socket);
                sendReceive.start();


            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_WAITING;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn= socket.getInputStream();
                tempOut= socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            Boolean disconnct = false;
            while (true)
            {
                try {

                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();

                    disconnct =true;
                    break;
                }


            }
            if(disconnct){

                disconnct =false;
                //Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                try {
                    ClientClass clientClass=new ClientClass(bluetoothDevice);
                    clientClass.start();
                    ServerClass serverClass = new ServerClass(true);
                    serverClass.start();
                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (status.getText().toString() == "Connected") {
            Intent intent = new Intent(this, BluetoothChatting.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenOn=true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenOn=false;
        storeMsg=new String[5];
        FiveNotify=1;
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}


