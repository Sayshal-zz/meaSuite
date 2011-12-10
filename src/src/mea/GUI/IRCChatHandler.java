package mea.GUI;

import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class IRCChatHandler extends JTextPane implements MeaChatFramework{
	public String tag = "[NoTag]";
	
	public IRCChatHandler(String tag) {
		this.tag = tag;
	}

	public boolean isTag(String line){
		if(tag == null || tag.equalsIgnoreCase("[notag]")){
			return true;
		}else{
			return line.startsWith(tag);
		}
	}

	public void append(String message) {
		MeaColoredChat.setColored(message, this);
	}
}
