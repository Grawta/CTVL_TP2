package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Monitor implements Runnable {
	Analyzer analyzer;
	ProxmoxAPI api;
	Map<String, List<LXC>> myCTsPerServer;

	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
		myCTsPerServer = new HashMap<String, List<LXC>>();
	}

	@Override
	public void run() {
		List<LXC> ListCts = new ArrayList<>();
		while (true) {
			try {
				ListCts.clear();
				
				
				List<LXC> cts = this.api.getCTs(Constants.SERVER1);
				for (LXC lxc : cts) {
					if (lxc.getName().contains(Constants.CT_BASE_NAME)) {							
						ListCts.add(lxc);
					}
				}
				this.myCTsPerServer.put(Constants.SERVER1, ListCts);
				ListCts = new ArrayList<>();
	
				cts = this.api.getCTs(Constants.SERVER2);
				for (LXC lxc : cts) {
					if (lxc.getName().contains(Constants.CT_BASE_NAME)) {						
						ListCts.add(lxc);
					}
				}
				this.myCTsPerServer.put(Constants.SERVER2, ListCts);

				
				this.analyzer.analyze(myCTsPerServer);
				
				this.myCTsPerServer.clear();
				
				try {
					Thread.sleep(Constants.MONITOR_PERIOD * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (LoginException | JSONException | IOException e1) {
				e1.printStackTrace();
			}

		}
	}
}
