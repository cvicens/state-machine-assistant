import java.util.Map;
import java.util.HashMap;
import java.util.Base64;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import org.apache.camel.Exchange;

import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;

import org.apache.camel.model.dataformat.JsonLibrary;

import org.apache.http.ProtocolException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;


public class HisToBot extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    
    from("kafka:{{kafka.topic}}?brokers={{kafka.bootstrap-servers}}&groupId={{kafka.groupId}}")
        .routeId("hl7-to-patient-info")
        .onException(ProtocolException.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "Error connecting to server, please check the application.properties file ${exception.message}")
            .end()
        .onException(HL7Exception.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "Error unmarshalling ${exception.message}")
            .end()
        .log("Route started from Telegram")
        .log("body: ${body}")
        .process(exchange -> {
            String encodedMessage = exchange.getIn().getBody(String.class);
            System.out.println("Encoded Message " + encodedMessage);

            byte[] decodedBytes = Base64.getDecoder().decode(encodedMessage);
            String decodedMessage = new String(decodedBytes);
            decodedMessage = decodedMessage.replaceAll("\n", "\r");
            System.out.println("Decoded Message " + decodedMessage);

            HapiContext context = new DefaultHapiContext();
            Parser p = context.getGenericParser();
            Message hapiMessage   = p.parse(decodedMessage);
            
            Terser terser = new Terser(hapiMessage);
            
            String sendingApplication = terser.get("/.MSH-3-1");
            System.out.println("sendingApplication " + sendingApplication);

            String msgCode = terser.get("/.MSH-9-1");
            String msgTriggerEvent = terser.get("/.MSH-9-2");
            
            System.out.println(">>> HL7 code: " + msgCode + " event: " + msgTriggerEvent);
            
            String surname = terser.get("/.PID-5-1");
            String name = terser.get("/.PID-5-2");
            String patientId = terser.get("/.PID-2-1") != null ? terser.get("/.PID-2-1") : terser.get("/.PID-3-1");
            String personalId = terser.get("/.PID-4-1") != null ? terser.get("/.PID-4-1") : terser.get("/.PID-3-1");
            
            Map<String, String> data = new HashMap<String, String>();

            String message = "Patient " + name + " " + surname + " with ID(" + personalId + ")" + " has been";
            if (msgTriggerEvent.equalsIgnoreCase("A01") || msgTriggerEvent.equalsIgnoreCase("A04")) {
                message += " admitted (" + msgTriggerEvent + ")";
            } else if (msgTriggerEvent.equalsIgnoreCase("A08")) {
                message += " updated (" + msgTriggerEvent + ")";
            } else if (msgTriggerEvent.equalsIgnoreCase("A03")) {
                message += " discharged (" + msgTriggerEvent + ")";
            } else {
                message += " taken care (" + msgTriggerEvent + ")";
            }

            data.put("message", message);
            data.put("personalId", "260677105"); // Change to personalId once Telegram Bot is ready
            data.put("patientId", patientId);

            exchange.getIn().setBody(data);
        })
        // marshall to JSON with GSON
        .marshal().json(JsonLibrary.Gson)
        .log("Converting to JSON data: ${body}")
        .convertBodyTo(String.class)
        .log("Sending message to telegram bot http://{{telegram-bot.host}}:{{telegram-bot.port}}/new-message: ${body}")
        .to("direct:send-patient-info-to-bot")
        .log("Patient info sent successfully: ${body}");

    from("direct:send-patient-info-to-bot")
        .routeId("send-patient-info-to-bot")
        .removeHeaders("*") // Otherwise you'll probably get a 400 error
        .setHeader("id", header(Exchange.TIMER_COUNTER))
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .setHeader(Exchange.HTTP_CHARACTER_ENCODING, constant("UTF-8"))
        .log("Executing saga #${headers.id} ${body}")
        .to("http4://{{telegram-bot.host}}:{{telegram-bot.port}}/new-message")
        .log("Patient info sent successfully: ${body}");
  }

  public class ChatBotLogic {
      public String chatBotProcess(String message) {
          //if( "do-not-reply".equals(message) ) {
          //    return null; // no response in the chat
          //}
          //return "echo from the bot: " + message; // echoes the message
          return message;
      }
  }

}