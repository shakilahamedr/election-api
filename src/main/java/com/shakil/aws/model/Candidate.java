package com.shakil.aws.model;

import com.google.gson.Gson;

public class Candidate {
	private int id;
	private String name;
	private String party;

	public Candidate(int id, String name, String party) {
		this.id = id;
		this.name = name;
		this.party = party;
	}

	public Candidate(String json) {
		Gson gson = new Gson();
		Candidate tempCandidate = gson.fromJson(json, Candidate.class);
		this.id = tempCandidate.id;
		this.name = tempCandidate.name;
		this.party = tempCandidate.party;
	}

	public String toString() {
		return new Gson().toJson(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}
}
