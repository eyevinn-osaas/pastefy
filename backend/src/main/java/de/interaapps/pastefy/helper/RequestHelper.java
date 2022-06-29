package de.interaapps.pastefy.helper;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.model.database.User;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.httpserver.Exchange;
import org.javawebstack.orm.query.Query;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    public static void queryFilter(Query<?> query, AbstractObject params) {
        Map<String, String> filters = new HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("filter[") && key.endsWith("]")) {
                filters.put(key.replace("filter[", "").replace("]", ""), value.string());
            }
        });
        if (filters.size() > 0)
            query.filter(filters);
    }

    public static void userIdPastesFilter(User user, Query<?> query, Exchange exchange) {
        boolean isAdmin = user != null && user.type == User.Type.ADMIN;
        String filterUserId = null;

        if (user == null && !Pastefy.getInstance().getConfig().get("pastefy.listpastes", "false").equalsIgnoreCase("true"))
            throw new PermissionsDeniedException();

        if (user != null && !isAdmin)
            filterUserId = user.id;

        if (isAdmin && exchange.query("user_id") != null)
            filterUserId = exchange.query("user_id");

        if (filterUserId != null)
            query.where("userId", filterUserId);
    }
}