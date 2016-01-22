package org.japhar81;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/{docType}")
public class JsonEndpoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getAll(@PathParam("docType") String docType) {
        JsonArray array = new JsonArray();
        JsonParser jsonParser = new JsonParser();
        try {
            ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("admin", "admin");
            try {
                for (ODocument doc : db.browseClass(docType)) {
                    array.add(jsonParser.parse(doc.toJSON()).getAsJsonObject());
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }

    @GET
    @Path("/{rId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOne(@PathParam("rId") String rid) {
        ORecord record = null;
        JsonParser jsonParser = new JsonParser();
        try {
            ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("admin", "admin");
            try {
                record = db.load(recordId(rid));
            } finally {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(record == null) {
            return Response.status(404).build();
        } else {
            return Response.ok(jsonParser.parse(record.toJSON()).getAsJsonObject()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@PathParam("docType") String docType, JsonObject obj) throws Exception {
        try {
            ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("admin", "admin");
            try {
                ODocument doc = new ODocument(docType);
                doc.fromJSON(obj.toString());
                doc.save();
            } finally {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/{rId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("docType") String docType,
                           @PathParam("rId") String rid,
                           JsonObject obj) {
        ORecord record;
        try {
            ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("admin", "admin");
            try {
                record = db.load(recordId(rid));
                if(record == null) {
                    return Response.status(404).build();
                } else {
                    record.fromJSON(obj.toString());
                    record.save();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("/{rId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("rId") String rid) {
        ORecord record;
        try {
            ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("admin", "admin");
            try {
                record = db.load(recordId(rid));
                if(record == null) {
                    return Response.status(404).build();
                } else {
                    record.delete();
                    return Response.ok().build();
                }
            } finally {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    public ORecordId recordId(String rid) {
        return new ORecordId("#"+rid);
    }
}
