package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;

	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}

	public void analyze(Map<String, List<LXC>> myCTsPerServer) throws LoginException, JSONException, IOException {
		Map<String, Float> lMemOnServers = new HashMap<String, Float>();
		float totalMem = 0.0f;
		float mem;
		List<LXC> value;
		String key;
		for (Iterator i = myCTsPerServer.entrySet().iterator(); i.hasNext();) {
			Entry couple = (Entry) i.next();
			key = (String) couple.getKey();
			value = (List<LXC>) couple.getValue();

			System.out.println("\n-> Serveur = " + key);

			lMemOnServers.put(key, (float) 0.0);
			for (LXC lxc : value) {
				System.out.println("Nom CT : " + lxc.getName());

				lMemOnServers.replace(key, lMemOnServers.get(key) + (float) lxc.getMaxmem());
			}
			System.out.println("\nRAM utilisée : " + lMemOnServers.get(key));
			// Calcul du ratio de mémoire RAM occupée par les CTs sur le serveur 'cle'
			lMemOnServers.replace(key, lMemOnServers.get(key) / (float) this.api.getNode(key).getMemory_total());
			
			System.out.println("Ratio RAM : " + lMemOnServers.get(key));
		}

		// M�moire autoris�e sur chaque serveur => 8% MAX
		float memRatio = 0.08f; // Ratio sur un serveur
		float totalMemRatio = 0.12f; // Ratio sur les deux serveurs

		for (Iterator i = lMemOnServers.entrySet().iterator(); i.hasNext();) {
			Entry couple = (Entry) i.next();

			key = (String) couple.getKey();
			mem = (Float) couple.getValue();

			if (mem > memRatio) {
				for (Iterator j = lMemOnServers.entrySet().iterator(); j.hasNext();) {
					Entry coupleJ = (Entry) j.next();

					String keyJ = (String) coupleJ.getKey();

					if (!key.matches(keyJ)) {
						System.out.println("\n## On migre de " + key + " à " + keyJ);
						try {
							this.controller.migrateFromTo(key, keyJ);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
			}
			totalMem += mem;
		}

		if (totalMem > totalMemRatio) {
			for (Iterator i = lMemOnServers.entrySet().iterator(); i.hasNext();) {
				Entry couple = (Entry) i.next();
				key = (String) couple.getKey();			
				System.out.println("\n Arret de " + key)	;
				this.controller.offLoad(key);
				break;
			}
		}
	}

}
