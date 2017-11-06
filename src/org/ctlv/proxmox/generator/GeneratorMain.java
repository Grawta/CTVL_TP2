package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.data.Node;
import org.json.JSONException;

public class GeneratorMain {

	static Random rndTime = new Random(new Date().getTime());

	public static int getNextEventPeriodic(int period) {
		return period;
	}

	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}

	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (-Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int) next;
	}

	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {

		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		int number = 0;

		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime());

		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);

		while (true) {
			// 1. Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
			float memOnServer1 = 0;
			float memOnServer2 = 0;
			// ...

			// M�moire autoris�e sur chaque serveur
			float memRatioOnServer1 = 0.16f;
			// ...
			float memRatioOnServer2 = 0.16f;
			// ...

			List<LXC> ctsSrv1 = api.getCTs("srv-px1");
			for (LXC lxc : ctsSrv1) {
				if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
					memOnServer1 += lxc.getMaxmem();
				}
			}
			List<LXC> ctsSrv2 = api.getCTs("srv-px2");
			for (LXC lxc : ctsSrv2) {
				if (lxc.getName().contains(Constants.CT_BASE_NAME)) {
					memOnServer2 += lxc.getMaxmem();
				}
			}
			memOnServer1 = memOnServer1/(float)api.getNode(Constants.SERVER1).getMemory_total();
			memOnServer2 = memOnServer2/(float)api.getNode(Constants.SERVER2).getMemory_total();

			if (memOnServer1 < memRatioOnServer1 && memOnServer2 < memRatioOnServer2) { // Exemple de condition de
																						// l'arr�t de la g�n�ration de
																						// CTs

				String serverName = rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1 ? Constants.SERVER1
						: Constants.SERVER2;
				api.createCT(serverName, Long.toString(Constants.CT_BASE_ID+number),
						Constants.CT_BASE_NAME + (Constants.CT_BASE_ID + number), 512);
				

				// attendre jusqu'au prochain �v�nement
				Thread.sleep(1000 * 30);
				api.startCT(serverName, Long.toString(Constants.CT_BASE_ID+number));
			} else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME * 1000);
			}
			number++;
		}

	}

}
