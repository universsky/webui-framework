package com.redhat.qe.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;

public class SCPTools {
	protected String userName;
	protected File sshPemFile;
	protected String password;
	protected String server;
	protected static Logger log = Logger.getLogger(SCPTools.class.getName());
	
	public SCPTools(String server,
			String user,
			File sshPemFile,
			String password){
		this.userName = user;
		this.sshPemFile = sshPemFile;
		this.password = password;
		this.server = server;
	}
	
	public SCPTools(String server,
			String user,
			String sshPemFileLoc,
			String password){
		this.userName = user;
		this.sshPemFile = new File(sshPemFileLoc);
		this.password = password;
		this.server = server;
	}
	
	public boolean sendFile(String source, String dest){
		
		return sendFile(source, dest, false);
	}
	
	public boolean sendFile(String source, String dest, boolean closeOnExit){
		log.info("SCP: Copying "+source+" to "+this.server+":"+dest);
		Connection newConn = connect_server();
		if(newConn ==null) return false;
		
		SCPClient scp = new SCPClient(newConn);
		try {
			scp.put(source, dest);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: File transfer failed:", e);
			return false;
		}
		log.info("SCP: Transfer succeeded");
		
		if(closeOnExit){
			try{
				newConn.close();
			}catch(Exception ex){
				log.severe("Error on closing server SSH connection."); // not so severe :)
			}
		}
		
		return true;		
	}
	
	/*public void sendStream(OutputStream os, String dest) throws IOException{
		Connection newConn = new Connection(server);
		log.info("SFTP: Copying stream to "+this.server+":"+dest);
		newConn.connect();
		newConn.authenticateWithPublicKey(userName, sshPemFile, password);
		SFTPv3Client sftp = new SFTPv3Client(newConn);
		sftp.createFile(dest);
		
		log.info("SFTP: Transfer succeeded");
	}*/
	
	public boolean getFile(String remoteFile, String target){
		
		log.info("SCP: Copying "+server+":"+remoteFile+" to "+target);
		Connection newConn = connect_server();
		if(newConn ==null) return false;

		SCPClient scp = new SCPClient(newConn);
		try {
			scp.get(remoteFile, target);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: File transfer failed:", e);
			return false;
		}
		log.info("SCP: Transfer succeeded");
		
		return true;
	}
	
	private Connection connect_server(){
		Connection newConn = new Connection(server);
		try {
			newConn.connect();
			newConn.authenticateWithPublicKey(userName, sshPemFile, password);
		} catch (IOException e) {
			newConn = new Connection(server);
			try{newConn.connect();}
			catch(IOException ioe){log.log(Level.INFO, "SCP: Connection failed:", ioe);}
			try{
				newConn.authenticateWithPassword(userName, password);
			} catch (IOException e1) {
				log.log(Level.INFO, "SCP: Connection failed:", e1);
				return null;
			}
		}
		return newConn;
	}
}
