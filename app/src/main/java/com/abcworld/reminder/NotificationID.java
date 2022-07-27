package com.abcworld.reminder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

class NotificationID {

   private final static AtomicInteger c = new AtomicInteger(0);

   public static int getID() {
      String s1 = String.valueOf(c.incrementAndGet());
      String s2 = new SimpleDateFormat("ddHHmmss",  Locale.KOREA).format(new Date());
      return Integer.parseInt(s1 + s2);
   }
}
