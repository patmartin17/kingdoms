package com.rivensoftware.hardcoresmp.storage.database;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.rivensoftware.hardcoresmp.HardcoreSMP;

import lombok.Getter;

public class KingdomsDatabase 
{
	@Getter private MongoClient client;

	@Getter private MongoDatabase database;

	@Getter private MongoCollection<Document> profiles;
	
	@Getter private MongoCollection<Document> houses;

	@Getter private MongoCollection<Document> fights;

	@Getter private MongoCollection<Document> capturePoints;

	@SuppressWarnings("deprecation")
	public KingdomsDatabase(HardcoreSMP plugin) 
	{
		if (plugin.getConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) 
		{
			this.client = new MongoClient(new ServerAddress(plugin.getConfig().getString("MONGO.HOST"), plugin.getConfig().getInt("MONGO.PORT")), Arrays.asList(new MongoCredential[] { MongoCredential.createCredential(plugin.getConfig().getString("MONGO.AUTHENTICATION.USER"), plugin.getConfig().getString("MONGO.AUTHENTICATION.DATABASE"), plugin.getConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()) }));
		} 
		else 
		{
			this.client = new MongoClient(new ServerAddress(plugin.getConfig().getString("MONGO.HOST"), plugin.getConfig().getInt("MONGO.PORT")));
		} 
		this.database = getClient().getDatabase(plugin.getConfig().getString("MONGO.DATABASE"));
		this.profiles = this.database.getCollection("profiles");
		this.houses = this.database.getCollection("houses");
		this.fights = this.database.getCollection("fights");
		this.capturePoints = this.database.getCollection("capture_points");
	}
}
