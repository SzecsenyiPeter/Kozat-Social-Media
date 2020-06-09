package com.mugi.peti.kozat.model;

public class ConversationHeader {

        public String userUId;
        public String dateToDisplay;
        public int numberOFUnread;
        public String lastMessage;

        public ConversationHeader( String userUId, String dateToDisplay, int numberOFUnread, String lastMessage) {
            this.userUId = userUId;
            this.dateToDisplay = dateToDisplay;
            this.numberOFUnread = numberOFUnread;
            this.lastMessage = lastMessage;
        }

}
