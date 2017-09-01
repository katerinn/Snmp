package com.enefyod.app;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnmpInterfacesListPrinter {

    private static Map<OID, String> oidMap;

    static {
        oidMap = new LinkedHashMap<>(22);
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.1"), "Index");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.2"), "Description");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.3"), "Type");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.4"), "MTU");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.5"), "Speed");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.6"), "PhysicalAddress");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.7"), "AdminStatus");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.8"), "OperativeStatus");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.9"), "LastChange");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.10"), "InOctets");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.11"), "InUcastPkts");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.12"), "InNUcastPkts");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.13"), "InDiscards");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.14"), "InErrors");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.15"), "InUnknownProtos");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.16"), "OutOctets");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.17"), "OutUcastPkts");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.18"), "OutNUcastPkts");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.19"), "OutDiscards");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.20"), "OutErrors");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.21"), "OutQLen");
        oidMap.put(new OID(".1.3.6.1.2.1.2.2.1.22"), "Specific");
    }

    public void printInterfacesList(String ipAddress, String port) {
        try {
            TransportMapping<UdpAddress> transport = getUdpAddressTransportMappingAndListen();
            TableUtils tableUtils = getTableUtils(transport);

            CommunityTarget communityTarget = getCommunityTarget(ipAddress, port);
            List<TableEvent> events = getTableEvents(communityTarget, tableUtils);

            printTableContent(events);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printTableContent(List<TableEvent> events) {
        for (TableEvent event : events) {
            if(event.isError()) {
                System.out.println(event.getErrorMessage());
                System.exit(-1);
            }
            for (VariableBinding vb : event.getColumns()) {
                if (vb != null) {
                    // Find OID name
                    String name = oidMap.get(extractTableColumnOID(vb));

                    if (Objects.equals(name, "Index")) {
                        System.out.print("Interface " + vb.getVariable().toString() + ": ");
                    } else {
                        System.out.print(name + "=\"" + vb.getVariable().toString() + "\" ");
                    }
                }
            }
            System.out.println("");
        }
    }

    private OID extractTableColumnOID(VariableBinding vb) {
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+){9}).*");
        Matcher matcher = pattern.matcher(vb.getOid().toString());
        if (matcher.find()) {
            return new OID(matcher.group(1));
        }
        throw new NoSuchElementException("Wrong OID format");
    }

    private List<TableEvent> getTableEvents(CommunityTarget communityTarget, TableUtils tableUtils) {
        OID[] oids = new OID[oidMap.size()];
        oidMap.keySet().toArray(oids);
        return tableUtils.getTable(communityTarget, oids, null, null);
    }

    private TableUtils getTableUtils(TransportMapping<UdpAddress> transport) {
        Snmp snmp = new Snmp(transport);
        return new TableUtils(snmp, new DefaultPDUFactory());
    }

    private CommunityTarget getCommunityTarget(String ipAddress, String port) {
        CommunityTarget communityTarget = new CommunityTarget();
        communityTarget.setCommunity(new OctetString("public"));
        communityTarget.setVersion(SnmpConstants.version2c);
        communityTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        communityTarget.setRetries(2);
        communityTarget.setTimeout(1000);
        return communityTarget;
    }

    private TransportMapping<UdpAddress> getUdpAddressTransportMappingAndListen() throws IOException {
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();
        return transport;
    }
}
