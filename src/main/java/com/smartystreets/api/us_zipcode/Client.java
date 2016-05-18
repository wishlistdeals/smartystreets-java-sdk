package com.smartystreets.api.us_zipcode;

import com.smartystreets.api.Request;
import com.smartystreets.api.Response;
import com.smartystreets.api.Sender;
import com.smartystreets.api.Serializer;
import com.smartystreets.api.exceptions.SmartyException;

import java.io.IOException;

public class Client {
    final String urlPrefix;
    private final Sender sender;
    final Serializer serializer;

    public Client(String urlPrefix, Sender sender, Serializer serializer) {
        this.urlPrefix = urlPrefix;
        this.sender = sender;
        this.serializer = serializer;
    }

    public void send(Lookup lookup) throws SmartyException, IOException {
        Batch batch = new Batch();
        batch.add(lookup);
        send(batch);
    }

    public void send(Batch batch) throws SmartyException, IOException {
        Request request = new Request(this.urlPrefix);

        if (batch.size() == 0)
            return;

        if (batch.size() == 1)
            this.populateQueryString(batch.get(0), request);
        else
            request.setPayload(this.serializer.serialize(batch.getAllLookups()));

        Response response = this.sender.send(request);
        Result[] results = this.serializer.deserialize(response.getPayload(), Result[].class);
        this.assignResultsToLookups(batch, results);
    }

    void populateQueryString(Lookup lookup, Request request) {
        request.putParameter("input_id", lookup.getInputId());
        request.putParameter("city", lookup.getCity());
        request.putParameter("state", lookup.getState());
        request.putParameter("zipcode", lookup.getZipCode());
    }

    void assignResultsToLookups(Batch batch, Result[] results) {
        for (int i = 0; i < results.length; i++) {
            batch.get(i).setResult(results[i]);
        }
    }
}
