package com.example.slack.springbootslack;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.view.Views.view;

import java.time.ZonedDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.event.AppHomeOpenedEvent;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.view.View;

@Configuration
	public class SlackApp {
	
	public AppConfig createAppConfig() {
		AppConfig config = new AppConfig();
		config.setSigningSecret("xyx");
		config.setSingleTeamBotToken("xoxb");
	    return config; // loads the env variables
	  }	
	
	  @Bean
	  public App initSlackApp() {
	    App app = new App(createAppConfig());
	    app.command("/greet", (req, ctx) -> {
	      return ctx.ack(" Hi, What's up?");
	    });
	    app.event(AppMentionEvent.class, (event, ctx) -> {
	        ctx.say("May I help you?");
	        return ctx.ack();
	      });
	    app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
			  // Build a Home tab view
			  ZonedDateTime now = ZonedDateTime.now();
			  View appHomeView = view(view -> view
			    .type("home")
			    .blocks(asBlocks(
			      section(section -> section.text(markdownText(mt -> mt.text(":wave: Hello, Notify Me App Home! (Last updated: " + now + ")")))),
			      //image(img-> img.imageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/EXL_Logo_RGB.jpg/440px-EXL_Logo_RGB.jpg")),
			      actions(actions -> actions
					        .elements(asElements(
					          button(b -> b.text(plainText(pt -> pt.text("Click me !"))).value("button1").actionId("button-action"))
					        ))
					      )
			    ))
			  );
	
	
			// Update the App Home for the given user
			  ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
			    .userId(payload.getEvent().getUser())
			    .hash(payload.getEvent().getView().getHash()) // To protect against possible race conditions
			    .view(appHomeView)
			  );
			  return ctx.ack();
			});
	    
	    app.blockAction("button-action", (req, ctx) -> {
	    	  String value = req.getPayload().getActions().get(0).getValue(); // "button's value"
	    	  if (req.getPayload().getResponseUrl() != null) {
	    	    // Post a message to the same channel if it's a block in a message
	    	    ctx.respond("You've sent " + value + " by clicking the button!");
	    	    
	    	  } 
	    	  System.out.println(req.getPayload()+" "+value);
	    	  return ctx.ack();
	    	});
	    app.attachmentAction("app", (req, ctx) -> {
	    	  String value = req.getPayload().getActions().get(0).getValue(); // "button's value"
	    	  if (req.getPayload().getResponseUrl() != null) {
	    	    // Post a message to the same channel if it's a block in a message
	    	    ctx.respond("You've sent " + value + " by clicking the button!");
	    	    
	    	  } 
	    	  System.out.println(req.getPayload()+" "+value);
	    	  return ctx.ack();
	    	});
	   
	 app.start();
	    
	    return app;
	    
	  }
}
