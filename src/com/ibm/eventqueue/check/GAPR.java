package com.ibm.eventqueue.check;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.Subject;

public class GAPR {
  public String makeConnection(String ObjectStore) throws IOException {
    Properties prop1 = new Properties();
    String propFileName = "config.properties";
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
    if (inputStream != null) {
      prop1.load(inputStream);
    } else {
      throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
    } 
    String url = prop1.getProperty("MTOMURL");
    String username = prop1.getProperty("Username");
    String password = prop1.getProperty("Password");
    String Query = prop1.getProperty("Query");
    String ObJectStore = ObjectStore;
    Connection connection = Factory.Connection.getConnection(url);
    Subject sub = UserContext.createSubject(connection, username, password, null);
    UserContext.get().pushSubject(sub);
    Domain domain = Factory.Domain.getInstance(connection, null);
    ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domain, ObJectStore, null);
    String mySQLString = Query;
    SearchSQL sqlObject = new SearchSQL(mySQLString);
    SearchScope searchScope = new SearchScope(objectStore);
    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
    int count = 0;
    String output = null;
    if (rowSet.isEmpty()) {
      System.out.println(String.valueOf(objectStore.get_Name()) + ":" + count);
      output = String.valueOf(objectStore.get_Name()) + ":" + count;
    } else {
      Iterator<RepositoryRow> iter = rowSet.iterator();
      while (iter.hasNext()) {
        RepositoryRow row = iter.next();
        count++;
      } 
      System.out.println(String.valueOf(objectStore.get_Name()) + ":" + count);
      output = String.valueOf(objectStore.get_Name()) + ":" + count;
    } 
    return "\n" + output;
  }
  
  public static void main(String[] args) throws IOException {
    GAPR gapr = new GAPR();
    String recipient = "DL-FileNetLightsOnSupport@anthem.com";
    String sender = "DL-FileNetLightsOnSupport@anthem.com";
    String host = "smtp.wellpoint.com";
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(properties);
    StringBuilder builder = new StringBuilder();
    String output = null;
    String[] count = new String[12];
    int totalcount = 0;
    String Status = null;
    builder.append("Hi Team,");
    builder.append("\n");
    builder.append("<br>");
    builder.append("Please find below the event queue count of the Object Stores.");
    builder.append("\n");
    builder.append("<br>");
    builder.append("<br>");
    builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>ObjectStore</th><th>Count</th></tr>");
    output = gapr.makeConnection("CENTRALCLAIMS");
    String[] outputsplit = output.split(":");
    count[0] = outputsplit[1];
    builder.append("<tr><td>" + outputsplit[0] + "</td>" + "<td>" + count[0] + "</td></tr>");
    output = gapr.makeConnection("WESTCLAIMS");
    String[] outputsplit1 = output.split(":");
    count[1] = outputsplit1[1];
    builder.append("<tr><td>" + outputsplit1[0] + "</td>" + "<td>" + count[1] + "</td></tr>");
    output = gapr.makeConnection("EASTCLAIMS");
    String[] outputsplit2 = output.split(":");
    count[2] = outputsplit2[1];
    builder.append("<tr><td>" + outputsplit2[0] + "</td>" + "<td>" + count[2] + "</td></tr>");
    output = gapr.makeConnection("FEP");
    String[] outputsplit3 = output.split(":");
    count[3] = outputsplit3[1];
    builder.append("<tr><td>" + outputsplit3[0] + "</td>" + "<td>" + count[3] + "</td></tr>");
    output = gapr.makeConnection("CFSHOME");
    String[] outputsplit4 = output.split(":");
    count[4] = outputsplit4[1];
    builder.append("<tr><td>" + outputsplit4[0] + "</td>" + "<td>" + count[4] + "</td></tr>");
    output = gapr.makeConnection("HR");
    String[] outputsplit5 = output.split(":");
    count[5] = outputsplit5[1];
    builder.append("<tr><td>" + outputsplit5[0] + "</td>" + "<td>" + count[5] + "</td></tr>");
    output = gapr.makeConnection("FINANCE");
    String[] outputsplit6 = output.split(":");
    count[6] = outputsplit6[1];
    builder.append("<tr><td>" + outputsplit6[0] + "</td>" + "<td>" + count[6] + "</td></tr>");
    output = gapr.makeConnection("LEGAL");
    String[] outputsplit7 = output.split(":");
    count[7] = outputsplit7[1];
    builder.append("<tr><td>" + outputsplit7[0] + "</td>" + "<td>" + count[7] + "</td></tr>");
    output = gapr.makeConnection("PROVIDER");
    String[] outputsplit8 = output.split(":");
    count[8] = outputsplit8[1];
    builder.append("<tr><td>" + outputsplit8[0] + "</td>" + "<td>" + count[8] + "</td></tr>");
    output = gapr.makeConnection("EFORMS");
    String[] outputsplit9 = output.split(":");
    count[9] = outputsplit9[1];
    builder.append("<tr><td>" + outputsplit9[0] + "</td>" + "<td>" + count[9] + "</td></tr>");
    output = gapr.makeConnection("RMFILEPLAN");
    String[] outputsplit10 = output.split(":");
    count[10] = outputsplit10[1];
    builder.append("<tr><td>" + outputsplit10[0] + "</td>" + "<td>" + count[10] + "</td></tr>");
    output = gapr.makeConnection("ENROLLMENT");
    String[] outputsplit11 = output.split(":");
    count[11] = outputsplit11[1];
    builder.append("<tr><td>" + outputsplit11[0] + "</td>" + "<td>" + count[11] + "</td></tr>");
    builder.append("</table></body></html>");
    builder.append("<br>");
    builder.append("<br>");
    builder.append("Regards,");
    builder.append("\n");
    builder.append("<br>");
    builder.append("FileNetLightsOn Team");
    builder.append("\n");
    builder.append("<br>");
    for (int i = 0; i < count.length; i++)
      totalcount += Integer.parseInt(count[i]); 
    if (totalcount <= 50) {
      Status = "Green";
    } else if (totalcount > 50 && totalcount < 100) {
      Status = "Amber";
    } else if (totalcount >= 100) {
      Status = "Red";
    } 
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom((Address)new InternetAddress(sender));
      message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient));
      String result = builder.toString();
      message.setContent(result, "text/html");
      message.setSubject("CE Event Queue Item Table Monitoring : " + Status);
      Transport.send((Message)message);
      System.out.println("Mail successfully sent");
    } catch (MessagingException mex) {
      mex.printStackTrace();
    } 
  }
}