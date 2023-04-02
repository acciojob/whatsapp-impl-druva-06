package com.driver;

import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;

    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name,String mobile) throws Exception {
        if(userMobile.contains(mobile)) throw new Exception("User already exists");
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        Group group;
        if(users.size() == 2){
            group = new Group(users.get(1).getName(),users.size());
        }
        else{
            customGroupCount++;
            group = new Group("Group "+customGroupCount,users.size());
        }
        groupUserMap.put(group,users);
        groupMessageMap.put(group,new ArrayList<>());
        return group;
    }

    public int createMessage(String content) {
        messageId++;
        Date date = new Date();
        new Message(messageId,content,date);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupMessageMap.containsKey(group)) throw new Exception("Group does not exist");
        boolean userExist = false;
        for(User user:groupUserMap.get(group)){
            if(Objects.equals(sender,user)) userExist = true;
        }
        if(!userExist) throw new Exception("You are not allowed to send message");
        groupMessageMap.get(group).add(message);
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!Objects.equals(approver,groupUserMap.get(group).get(0))) throw new Exception("Approver does not have rights");
        int idx = 0;
        for(User user1:groupUserMap.get(group)){
            if(Objects.equals(user1,user)){
                break;
            }
            idx++;
        }
        if(idx==groupUserMap.get(group).size()) throw new Exception("User is not a participant");
        groupUserMap.get(group).set(idx,approver);
        groupUserMap.get(group).set(0,user);
        return "SUCCESS";
    }
}
