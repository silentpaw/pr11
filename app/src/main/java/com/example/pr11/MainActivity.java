package com.example.pr11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private Button buttonLoadPage;
    private MediaPlayer mediaPlayer;
    public  static final String CHANNEL_ID = "example_channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        buttonLoadPage = findViewById(R.id.button_load_page);

        // Включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Помогает приложению открывать ссылки внутри WebView, а не во внешнем браузере
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Скрываем кнопку после завершения загрузки страницы
                buttonLoadPage.setVisibility(View.GONE);
            }
        });

        // Устанавливаем слушатель для кнопки
        buttonLoadPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Загрузка страницы при нажатии на кнопку
                webView.loadUrl("https://online-edu.mirea.ru");
            }
        });

        // Создаем объект MediaPlayer
        mediaPlayer = new MediaPlayer();

        // Устанавливаем атрибуты аудио для потокового воспроизведения
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        // Устанавливаем источник данных для MediaPlayer
        try {
            mediaPlayer.setDataSource("https://soundcloud.com/rusyarapscene/sidodgi-duboshit-hell-star");
            // Асинхронно подготавливаем MediaPlayer
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Устанавливаем слушатель, который будет вызываться, когда MediaPlayer подготовится к воспроизведению
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Музыка готова к воспроизведению, вы можете вызвать start() для начала воспроизведения
                mediaPlayer.start();
            }
        });

        ImageView imageView = findViewById(R.id.cats_view);
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotateAnim.setDuration(2000);
        rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnim.setRepeatMode(ObjectAnimator.RESTART);
        rotateAnim.start();
        createNotificationChannel();
        final Button notifyButton = findViewById(R.id.notifyButton);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder = new
                        NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)

                        .setSmallIcon(R.drawable.cats)
                        .setContentTitle("Example Notification")
                        .setContentText("This is a test notification")

                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManager notificationManager =
                        getSystemService(NotificationManager.class);
                notificationManager.notify(1,
                        builder.build());
            }
        });
        final Button delayedNotifyButton = findViewById(R.id.delayedNotifyButton);
        delayedNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleNotification(10000); // 10 секунд
            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Delayed Notifications";
            String description = "Channel for delayed example notifications";
            int importance =
                    NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new
                    NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }
    private void scheduleNotification(long delay) {
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager)
                getSystemService(ALARM_SERVICE);
        long futureInMillis = System.currentTimeMillis() +
                delay;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                futureInMillis, pendingIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

