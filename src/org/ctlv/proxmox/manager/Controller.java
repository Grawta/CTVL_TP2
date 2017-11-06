package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;

	public Controller(ProxmoxAPI api) {
		this.api = api;
	}

	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException, InterruptedException {
		LXC mLXC = null;

		List<LXC> cts = this.api.getCTs(srcServer);
		for (LXC lxc : cts) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
				System.out.println("Migration du CT : " + lxc.getName());

				mLXC = lxc;
				break;
			}
		}
		this.api.stopCT(srcServer, mLXC.getVmid());
		Thread.sleep(10000);
		this.api.migrateCT(srcServer, mLXC.getVmid(), dstServer);
		Thread.sleep(25000);
		this.api.startCT(dstServer, mLXC.getVmid());

	}

	public void offLoad(String server) throws LoginException, JSONException, IOException {
		int min = 11199;
		LXC oldLxc = null;

		List<LXC> cts = this.api.getCTs(server);
		for (LXC lxc : cts) {
			if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
				int numCT = Integer.parseInt(lxc.getName().substring(20));
				if (min >= numCT) {
					min = numCT;
					oldLxc = lxc;
				}
			}
		}

		System.out.println("Deconnexion du CT : " + oldLxc.getName());
		api.stopCT(server, oldLxc.getVmid());
	}

}
