/*
 * MIT License
 *
 * Copyright (c) 2018, Apptastic Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.apptastic.tickersymbol.provider;

import com.apptastic.tickersymbol.Source;
import com.apptastic.tickersymbol.TickerSymbol;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.*;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Ticker provider implementation that fetches ticker information from Nasdaq OMX Nordic.
 * Nasdaq OMX Nordic is a stock exchange for swedish stocks and nordic stocks.
 */
public class NasdaqOmxNordic extends AbstractHttpsConnection implements TickerSymbolProvider {
    private static final String URL = "http://www.nasdaqomxnordic.com/webproxy/DataFeedProxy.aspx";
    private static final String HTTP_POST_BODY = "<post>\n" +
            "<param name=\"SubSystem\" value=\"Prices\"/>\n" +
            "<param name=\"Action\" value=\"Search\"/>\n" +
            "<param name=\"inst.an\" value=\"nm,fnm,isin,tp,cr,chp,tb,mkt,st,isrid,ec,isr\"/>\n" +
            "<param name=\"List\" value=\"M:INET:XSTO:SEEQ-SHR,M:INET:XSTO:SEEQ-SHR-CCP,M:INET:XSTO:SEEQ-SHR-IC,M:INET:XCSE:DKEQ-SHR,M:INET:XCSE:DKEQ-SHR-CCP,M:INET:XCSE:DKEQ-SHR-IC,M:INET:XHEL:FIEQ-SHR,M:INET:XHEL:FIEQ-SHR-CCP,M:INET:XHEL:FIEQ-SHR-IC,M:INET:XICE:ISEQ-SHR,M:INET:XSTO:SEEQ-SHR-NOK,M:INET:XSTO:SEEQ-SHR-AO,M:INET:FNSE:SEMM-NM,M:INET:FNDK:FNDK-CPH,M:INET:FNFI:SEMM-FN-HEL,M:INET:FNIS:ISEC-SHR,M:INET:FNSE:SEMM-FN-NOK,M:INET:FNEE:EEMM-SHR,M:INET:FNLV:LVMM-SHR,M:INET:FNLT:LTMM-SHR,M:INET:FNFI:SEMM-FN-HE-ERW,M:INET:XTAL:EEEQ-SHR,M:INET:XRIS:LVEQ-SHR,M:INET:XLIT:LTEQ-SHR,M:GITS:RI:RSEBA,M:GITS:TA:TSEBA,M:GITS:VI:VSEBA,INET:XSTO:SEEQXFUNOK,INET:XSTO:SEEQXFU,INET:XSTO:SEEQXFUNOK,INET:XHEL:FIEQFUI,M:INET:XSTO:SEEQ-AIF,M:INET:XCSE:DKEQ-OCIS,M:INET:XCSE:DKEQ-AIF-NMM,M:INET:XCSE:DKEQ-UTC-NMM,M:INET:XCSE:DKEQ-UTC,M:INET:XCSE:DKEQ-FUI,M:INET:FNSE:STO-CERT,M:INET:FNFI:HEL-CERT,L:INET:H11074310,L:INET:H11074310,M:INET:FNDK:CPH-CERT,M:INET:FNSE:STO-WAR,M:INET:FNFI:HEL-WAR,M:INET:FNDK:CPH-WAR,M:INET:FNSE:OSL-WAR,M:INET:FNSE:STO-WAR-X,M:INET:FNFI:HEL-WAR-X,M:INET:FNDK:CPH-WAR-X,GITS:SE:SEI,GITS:SE:SES,GITS:SE:DAI,GITS:SE:DAS,GITS:SE:HXS,GITS:SE:HXSOR,GITS:SE:EUI,GITS:SE:NNOI,M:GITS:SE:NOS,GITS:BONDPRICE30426,GITS:BONDYIELD87750,GITS:RETAILCORPORATEBONDSPRICE118,GITS:STRUCTUREDPRODUCTS30428,GITS:SUBORDINATEDLOANS30427,M:GITS:CO:CPHCB,M:GITS:FC:CPHFB,M:GITS:CO:CPHAU,GITS:BSTR,GITS:BCOR,GITS:HEBE,GITS:HECV,GITS:HE:HELRS,M:GITS:ST:STOCB,M:GITS:ST:STOGB,M:GITS:ST:STOMU,M:GITS:ST:STORB,M:GITS:ST:STOMB,M:GITS:FS:STOFR,M:GITS:FS:STOFU,M:GITS:FS:STOFC,M:GITS:FS:STOFI,M:GITS:ST:STOST,M:GITS:ST:STOLB,M:GITS:RI:RSEBA,M:GITS:TA:TSEBA,M:GITS:VI:VSEBA,INET:XCSE:DKEQUTC,GlobalIndex,INET:XSTO:SEEQEQR,INET:XCSE:DKEQEQR,INET:XHEL:FIEQEQR,M:INET:FNSE:STO-LEV,M:INET:FNFI:HEL-LEV,M:INET:FNDK:CPH-LEV,M:INET:FNSE:STO-LEV-X,M:INET:FNDK:CPH-LEV-X,M:INET:FNFI:HEL-LEV-X,M:INET:FNSE:STO-TRA,M:INET:FNSE:STO-TNM,M:INET:FNFI:HEL-TRA,M:INET:FNDK:CPH-TRA,M:INET:FNFI:HEL-TRA-X,M:INET:FNDK:CPH-TRA-X,M:INET:FNSE:STO-TRA-X,,\"/>\n" +
            "<param name=\"InstrumentISIN\" value=\"%1$s\"/>\n" +
            "<param name=\"InstrumentName\" value=\"%2$s\"/>\n" +
            "<param name=\"InstrumentFullName\" value=\"%3$s\"/>\n" +
            "<param name=\"json\" value=\"1\"/>\n" +
            "<param name=\"app\" value=\"/\"/>\n" +
            "</post>";

    /**
     * Search ticker by name.
     * @param name name.
     * @return stream of tickers
     */
    @Override
    public List<TickerSymbol> searchByName(String name) {
        return Collections.emptyList();
    }

    /**
     * Search ticker by ISIN code.
     * @param isin ISIN code.
     * @return stream of tickers
     * @throws IOException IO exception
     */
    public List<TickerSymbol> searchByIsin(String isin) throws IOException {
        String postBody =  String.format(HTTP_POST_BODY, isin, "", "");
        postBody = "xmlquery=" + URLEncoder.encode(postBody, "UTF-8");

        try (BufferedReader reader = sendRequest(URL, postBody.getBytes(), "UTF-8")) {
            return handleResponse(reader);
        }
    }

    @Override
    protected void setRequestHeaders(HttpRequest.Builder requestBuilder) {
        super.setRequestHeaders(requestBuilder);

        requestBuilder.header("Accept", "*/*");
        requestBuilder.header("Accept-Language", "en-GB,en;q=0.9,en-US;q=0.8,sv;q=0.7");
        requestBuilder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        requestBuilder.header("X-Requested-With", "XMLHttpRequest");
    }

    private List<TickerSymbol> handleResponse(BufferedReader reader) throws IOException {
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(true);

        if (jsonReader.peek() == JsonToken.STRING)
            return Collections.emptyList();

        List<TickerSymbol> tickers = new ArrayList<>();
        JsonUtil.optBeginObject(jsonReader);

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if ("inst".equals(name))
                parseTickers(jsonReader, tickers);
            else
                jsonReader.skipValue();
        }

        JsonUtil.optEndObject(jsonReader);
        return tickers;
    }

    @Override
    protected void parseTicker(JsonReader reader, TickerSymbol ticker) throws IOException {
        String name = reader.nextName();

        if ("@nm".equals(name)) {
            ticker.setSymbol(reader.nextString());
            ticker.setSource(Source.NASDAQ_OMX_NORDIC);
        }
        else if ("@fnm".equals(name))
            ticker.setName(reader.nextString());
        else if ("@isin".equals(name))
            ticker.setIsin(reader.nextString());
        else if ("@cr".equals(name))
            ticker.setCurrency(reader.nextString());
        else if ("@mkt".equals(name))
            ticker.setMic(getMic(reader.nextString()));
        else if ("@st".equals(name))
            ticker.setDescription(JsonUtil.nextOptString(reader, ""));
        else
            reader.skipValue();
    }

    private String getMic(String text) {
        String[] mkt = text.split(":");

        if (mkt.length <= 2)
            return null;

        return mkt[2];
    }
}
