/* This class represents the response to a subscribe request
 * 
 * Author: Luca Roffia (luca.roffia@unibo.it)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package it.unibo.arces.wot.sepa.commons.response;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;

// TODO: Auto-generated Javadoc
/**
 * This class represents the response to a SPARQL 1.1 Subscribe (see SPARQL 1.1
 * Subscription Language)
 *
 * The JSON serialization is the following:
 *
 * {"subscribed" : {"spuid":"SPUID","alias":"ALIAS","firstResults":<BindingsResults>}}
 *
 */

public class SubscribeResponse extends Response {

	public SubscribeResponse(JsonObject response){
		json = response;
	}

	public BindingsResults getBindingsResults() {
		return new BindingsResults(json.get("subscribed").getAsJsonObject().get("firstResults").getAsJsonObject());
	}
	
	/**
	 * Instantiates a new subscribe response.
	 *
	 * @param token
	 *            the token
	 * @param spuid
	 *            the spuid
	 */
	public SubscribeResponse(Integer token, String spuid,BindingsResults firstResults) {
		super(token);

		JsonObject response = new JsonObject();
		
		if (spuid != null)
			response.add("spuid", new JsonPrimitive(spuid));
		
		response.add("firstResults", new BindingsResults(firstResults).toJson());
		
		json.add("subscribed", response);
	}

	/**
	 * Instantiates a new subscribe response.
	 *
	 * @param token
	 *            the token
	 * @param spuid
	 *            the spuid
	 * @param alias
	 *            the alias
	 */
	public SubscribeResponse(Integer token, String spuid, String alias,BindingsResults firstResults) {
		super(token);

		JsonObject response = new JsonObject();
		
		if (spuid != null)
			response.add("spuid", new JsonPrimitive(spuid));
		if (alias != null)
			response.add("alias", new JsonPrimitive(alias));
		response.add("firstResults", new BindingsResults(firstResults).toJson());
		
		json.add("subscribed", response);
	}

	/**
	 * Instantiates a new subscribe response.
	 *
	 * @param spuid
	 *            the spuid
	 */
	public SubscribeResponse(String spuid) {
		super();

		JsonObject response = new JsonObject();
		
		if (spuid != null)
			response.add("spuid", new JsonPrimitive(spuid));
		
		json.add("subscribed", response);
	}

	/**
	 * Instantiates a new subscribe response.
	 */
	public SubscribeResponse() {
		super();
	}

	/**
	 * Instantiates a new subscribe response.
	 *
	 * @param spuid
	 *            the spuid
	 * @param alias
	 *            the alias
	 */
	public SubscribeResponse(String spuid, String alias) {
		super();

		JsonObject response = new JsonObject();
		
		if (spuid != null)
			response.add("spuid", new JsonPrimitive(spuid));
		if (alias != null)
			response.add("alias", new JsonPrimitive(alias));
		
		json.add("subscribed", response);
	}

	/**
	 * Gets the spuid.
	 *
	 * @return the spuid
	 */
	public String getSpuid() {
		try {
			return json.get("subscribed").getAsJsonObject().get("spuid").getAsString();
		}
		catch(Exception e) {
			return "";
		}
	}

	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public String getAlias() {
		try {
			return json.get("subscribed").getAsJsonObject().get("alias").getAsString();
		}
		catch(Exception e) {
			return "";
		}
	}
}
