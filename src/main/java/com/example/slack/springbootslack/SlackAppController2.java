package com.example.slack.springbootslack;

import javax.servlet.annotation.WebServlet;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

@WebServlet("/slack/actions")

	public class SlackAppController2 extends SlackAppServlet {
	  public SlackAppController2(App app) {
	    super(app);
	  }
}
