import java.util.Map;
import java.util.HashMap;

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
    // Temporary from telegram, in short this should be kafka
    from("telegram:bots/774448187:AAFWAZFI_2EaQE4UtyJDHIxB2PIIU9K8z08").convertBodyTo(String.class)
                .routeId("his-2-bot")
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
                // very simple mapping from a HLV2 patient to Telegram message
                .process(exchange -> {
                    String msg = exchange.getIn().getBody(String.class);
                    msg = msg.replaceAll("\n", "\r");                    

                    HapiContext context = new DefaultHapiContext();
                    Parser p = context.getGenericParser();
                    Message hapiMsg   = p.parse(msg);
                    
                    Terser terser = new Terser(hapiMsg);
                    
                    String sendingApplication = terser.get("/.MSH-3-1");
                    System.out.println("sendingApplication " + sendingApplication);

                    String msgCode = terser.get("/.MSH-9-1");
                    String msgTriggerEvent = terser.get("/.MSH-9-2");
                    
                    System.out.println("msgCode: " + msgCode + " msgTriggerEvent: " + msgTriggerEvent);
                    
                    String surname = terser.get("/.PID-5-1");
                    System.out.println("surname " + surname);
                    String name = terser.get("/.PID-5-2");
                    String patientId = terser.get("/.PID-2-1") != null ? terser.get("/.PID-2-1") : terser.get("/.PID-3-1");
                    
                    Map<String, String> data = new HashMap<String, String>();

                    String message = "Patient " + name + " " + surname + " has been";
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
                    data.put("patientId", patientId);

                    exchange.getIn().setBody(data);
                })
                // marshall to JSON with GSON
                .marshal().json(JsonLibrary.Gson)
                .log("Converting to String JSON data: ${body}")
                .convertBodyTo(String.class)
                //.to("telegram:bots/829504574:AAEjoaDiD0118_YFI88g94CI5eIfo7wCnpY")
                // TODO: use patient ID instead of constant chat ID!!!!
                .setHeader(Exchange.HTTP_PATH, constant("260677105"))
                .log("Sending message to telegram bot {{telegram-bot.host}}:{{telegram-bot.port}}: ${body}")
                .to("http://{{telegram-bot.host}}:{{telegram-bot.port}}/new-message/") 
                // log the outcome
                .log("Patient created successfully: ${body}");
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