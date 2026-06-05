package com.chaty.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "conversation_members", joinColumns = @JoinColumn(name = "conversation_id"))
    @Column(name = "member_id")
    private List<String> members;

    public Conversation() {}

    public Conversation(List<String> members) {
        this.members = members;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
}
