package org.ctlv.proxmox.tester;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.data.Node;
import org.ctlv.proxmox.manager.ManagerMain;
import org.json.JSONException;

public class Main {

	public static void main(String[] args) throws Exception {

//		ProxmoxAPI api = new ProxmoxAPI();
//
//		List<LXC> cts = api.getCTs("srv-px1");
//		for (LXC lxc : cts) {
//			System.out.println(lxc.getName());
//		}
//		Node srv1 = api.getNode(Constants.SERVER1);
//		System.out.println("Memory use % : " + 100 * srv1.getMemory_used() / srv1.getMemory_total() + "%");
//		System.out.println("CPU use % : " + 100 * srv1.getCpu() + "%");
//		System.out.println("Disk usage % : " + 100 * srv1.getRootfs_used() / srv1.getRootfs_total() + "%");
//		// Create CT
//		api.createCT(Constants.SERVER1, Long.toString(Constants.CT_BASE_ID),
//				Constants.CT_BASE_NAME + Constants.CT_BASE_ID, 512);
//
//		List<LXC> ct = api.getCTs("srv-px1");
//		LXC myCt = null;
//		for (LXC lxc : ct) {
//			if(lxc.getName().equals(Constants.CT_BASE_NAME + Constants.CT_BASE_ID)) {
//				myCt = lxc;
//			}
//		}
//		Thread.sleep(1000*Constants.GENERATION_WAIT_TIME);
//		System.out.println("CPU usage CT : "+ myCt.getCpu()+"%");
//		System.out.println("Status CT : "+ myCt.getStatus()+"%");
//		System.out.println("Mem usage CT : "+ 100*myCt.getMem()/myCt.getMaxmem()+"%");
//		System.out.println("Disk CT % : "+100*myCt.getDisk()/myCt.getMaxdisk()+"%");
		
		
		
		
		//Attention problème lors de la création si il y a déjà des containers du meme noms
		ManagerMain.main(null);

	}

}
