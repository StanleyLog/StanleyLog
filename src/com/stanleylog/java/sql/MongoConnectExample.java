package com.stanleylog.java.sql;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;

public class MongoConnectExample {

	static final String MONGO_DB_NAME = "test";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.out.println("MongoDB Test Begin.");

		MongoClientOptions options = MongoClientOptions.builder()
				.writeConcern(WriteConcern.MAJORITY)
				.writeConcern(WriteConcern.JOURNAL_SAFE)
				.writeConcern(WriteConcern.FSYNC_SAFE).build();

		Mongo mongo = null;
		DB db = null;

		try {
			mongo = new MongoClient("localhost", options);
			db = mongo.getDB(MONGO_DB_NAME);

			for (String collectionName : db.getCollectionNames()) {
				System.out.println(collectionName);
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (db != null) {
				db = null;
			}
			if (mongo != null) {
				mongo.close();
			}
		}

		// System.out.println("MongoDB Test End.");
	}

}
