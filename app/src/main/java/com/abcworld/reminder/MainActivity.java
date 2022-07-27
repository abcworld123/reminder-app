package com.abcworld.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String title = "리마인더";

		EditText etTitle = findViewById(R.id.editTextTitle);
		EditText etContent = findViewById(R.id.editTextContent);
		EditText etGroup = findViewById(R.id.editTextGroup);
		RadioGroup radioGroup = findViewById(R.id.radioGroup);
		Button btn = findViewById(R.id.buttonSave);

		radioGroup.check(R.id.radioMedium);

		btn.setOnClickListener(v -> {
//			String title = etTitle.getText().toString();
			String body = etContent.getText().toString();
//			String big = etGroup.getText().toString();
			sendNotification(title, body);
		});
	}


	private void sendNotification(String messageTitle, String messageBody) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE);
		String channelId = getString(R.string.default_notification_channel_id);
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.drawable.bell)
						.setContentTitle(messageTitle)
						.setContentText(messageBody)
						.setAutoCancel(false)
						.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		String channelName = "일반 알림";
		NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
		notificationManager.createNotificationChannel(channel);

		notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
	}
}
