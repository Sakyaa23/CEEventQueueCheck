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

public class EQ {
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
      output = String.valueOf(objectStore.get_Name()) + ":" + count;
    } else {
      Iterator<RepositoryRow> iter = rowSet.iterator();
      while (iter.hasNext()) {
        RepositoryRow row = iter.next();
        count++;
      } 
      output = String.valueOf(objectStore.get_Name()) + ":" + count;
    } 
    return "\n" + output;
  }
  
  public static void main(String[] args) throws IOException {
    EQ eqe = new EQ();
    String recipient = "DL-FileNetLightsOnSupport@anthem.com";
    String sender = "DL-FileNetLightsOnSupport@anthem.com";
    String host = "smtp.wellpoint.com";
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(properties);
    StringBuilder builder = new StringBuilder();
    String[] output = new String[12];
    int totalcount = 0;
    int i = 0;
    String Status = null;
    output[11] = eqe.makeConnection("WESTCLAIMS");
    output[0] = eqe.makeConnection("PROVIDER");
    output[1] = eqe.makeConnection("CENTRALCLAIMS");
    output[2] = eqe.makeConnection("EASTCLAIMS");
    output[3] = eqe.makeConnection("ENROLLMENT");
    output[4] = eqe.makeConnection("FEP");
    output[5] = eqe.makeConnection("CFSHOME");
    output[6] = eqe.makeConnection("HR");
    output[7] = eqe.makeConnection("FINANCE");
    output[8] = eqe.makeConnection("LEGAL");
    output[9] = eqe.makeConnection("eForms");
    output[10] = eqe.makeConnection("RMFilePlan");
    builder.append("<html><body><br>Hi Team,\n<br>Please find below the event queue count of the Object Stores.\n<br><table border=1><tr bgcolor='#b2beb5'><th>Object Store</th><th>Count</th>");
    for (i = 0; i < 12; i++) {
      System.out.println(output[i]);
      String[] outputsplit = output[i].split(":");
      String OS = outputsplit[0];
      int count = Integer.parseInt(outputsplit[1]);
      totalcount += count;
      builder.append("<tr><td>" + OS + "</td>" + "<td>" + count + "</td></tr>");
    } 
    builder.append("</table></body></html>");
    builder.append("Regards,");
    builder.append("\n");
    builder.append("<br>");
    builder.append("FileNetLightsOn Team");
    builder.append("\n");
    builder.append("<br>");
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom((Address)new InternetAddress(sender));
      message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient));
      String result = builder.toString();
      message.setContent(result, "text/html");
      if (totalcount > 50) {
        Status = "amber";
        message.setSubject("CE Event Queue Item Table Monitoring : " + Status);
      } else {
        Status = "green";
        message.setSubject("CE Event Queue Item Table Monitoring : " + Status);
      } 
      Transport.send((Message)message);
      System.out.println("Mail successfully sent");
    } catch (MessagingException mex) {
      mex.printStackTrace();
    } 
  }
}
