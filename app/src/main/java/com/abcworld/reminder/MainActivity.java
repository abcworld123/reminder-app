package com.abcworld.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


enum Priority { HIGH, MEDIUM }


public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		EditText etTitle = findViewById(R.id.editTextTitle);
		EditText etContent = findViewById(R.id.editTextContent);
		RadioGroup radioGroup = findViewById(R.id.radioGroup);
		Button btn = findViewById(R.id.buttonSave);

		radioGroup.check(R.id.radioMedium);

		btn.setOnClickListener(v -> {
			String title = etTitle.getText().toString();
			String body = etContent.getText().toString();
			int checked = radioGroup.getCheckedRadioButtonId();

			if (title.equals("")) {
				title = "리마인더";
			}

			if (checked == R.id.radioHigh) {
				notify(title, body, Priority.HIGH);
			} else {
				notify(title, body, Priority.MEDIUM);
			}
		});
	}


	private void notify(String title, String content, Priority priority) {
		final String channelId = getString(R.string.notification_channel_id);
		final String channelName = "알림";
		int groupId, icon;
		String group;

		if (priority == Priority.HIGH) {
			group = "com.abcworld.reminder.high";
			groupId = 1;
			icon = R.drawable.siren;
		} else {
			group = "com.abcworld.reminder.medium";
			groupId = 2;
			icon = R.drawable.ic_stat_bell;
		}

		NotificationCompat.Builder groupBuilder = new NotificationCompat.Builder(this, channelId)
				.setSmallIcon(icon)
				.setAutoCancel(true)
				.setOnlyAlertOnce(true)
				.setGroup(group)
				.setGroupSummary(true);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(content)
				.setGroup(group)
				.setAutoCancel(true);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
		notificationManager.createNotificationChannel(channel);

		notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
		notificationManager.notify(groupId, groupBuilder.build());
	}
}
