//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.hadoop.hdfs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.protobuf.BlockingService;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.http.HttpConfig.Policy;
import org.apache.hadoop.http.HttpServer2.Builder;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.RpcKind;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.ToolRunner;

public class DFSUtil {
    public static final Log LOG = LogFactory.getLog(DFSUtil.class.getName());
    private static final ThreadLocal<SecureRandom> SECURE_RANDOM = new ThreadLocal<SecureRandom>() {
        protected SecureRandom initialValue() {
            return new SecureRandom();
        }
    };
    static final DFSUtil.AddressMatcher LOCAL_ADDRESS_MATCHER = new DFSUtil.AddressMatcher() {
        public boolean match(InetSocketAddress s) {
            return NetUtils.isLocalAddress(s.getAddress());
        }
    };
    public static final Options helpOptions = new Options();
    public static final Option helpOpt = new Option("h", "help", false, "get help information");

    private DFSUtil() {
    }

    public static SecureRandom getSecureRandom() {
        return (SecureRandom)SECURE_RANDOM.get();
    }

    public static <T> T[] shuffle(T[] array) {
        if (array != null && array.length > 0) {
            int n = array.length;

            while(n > 1) {
                int randomIndex = ThreadLocalRandom.current().nextInt(n);
                --n;
                if (n != randomIndex) {
                    T tmp = array[randomIndex];
                    array[randomIndex] = array[n];
                    array[n] = tmp;
                }
            }
        }

        return array;
    }

    public static boolean isValidName(String src) {
        return DFSUtilClient.isValidName(src);
    }

    public static boolean isValidNameForComponent(String component) {
        if (!component.equals(".") && !component.equals("..") && component.indexOf(":") < 0 && component.indexOf("/") < 0) {
            return !isReservedPathComponent(component);
        } else {
            return false;
        }
    }

    public static boolean isReservedPathComponent(String component) {
        String[] var1 = HdfsServerConstants.RESERVED_PATH_COMPONENTS;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String reserved = var1[var3];
            if (component.equals(reserved)) {
                return true;
            }
        }

        return false;
    }

    public static String bytes2String(byte[] bytes) {
        return bytes2String(bytes, 0, bytes.length);
    }

    public static String bytes2String(byte[] bytes, int offset, int length) {
        return DFSUtilClient.bytes2String(bytes, 0, bytes.length);
    }

    public static byte[] string2Bytes(String str) {
        return DFSUtilClient.string2Bytes(str);
    }

    public static String byteArray2PathString(byte[][] components, int offset, int length) {
        int range = offset + length;
        Preconditions.checkPositionIndexes(offset, range, components.length);
        if (length == 0) {
            return "";
        } else {
            byte[] firstComponent = components[offset];
            boolean isAbsolute = offset == 0 && (firstComponent == null || firstComponent.length == 0);
            if (offset == 0 && length == 1) {
                return isAbsolute ? "/" : bytes2String(firstComponent);
            } else {
                int pos = isAbsolute ? 0 : firstComponent.length;
                int size = pos + length - 1;

                for(int i = offset + 1; i < range; ++i) {
                    size += components[i].length;
                }

                byte[] result = new byte[size];
                if (!isAbsolute) {
                    System.arraycopy(firstComponent, 0, result, 0, firstComponent.length);
                }

                for(int i = offset + 1; i < range; ++i) {
                    result[pos++] = 47;
                    int len = components[i].length;
                    System.arraycopy(components[i], 0, result, pos, len);
                    pos += len;
                }

                return bytes2String(result);
            }
        }
    }

    public static String byteArray2PathString(byte[][] pathComponents) {
        return byteArray2PathString(pathComponents, 0, pathComponents.length);
    }

    public static String strings2PathString(String[] components) {
        if (components.length == 0) {
            return "";
        } else {
            return components.length != 1 || components[0] != null && !components[0].isEmpty() ? Joiner.on("/").join(components) : "/";
        }
    }

    public static String path2String(Object path) {
        return path == null ? null : (path instanceof String ? (String)path : (path instanceof byte[][] ? byteArray2PathString((byte[][])((byte[][])path)) : path.toString()));
    }

    public static byte[][] getPathComponents(String path) {
        byte[] bytes = string2Bytes(path);
        return bytes2byteArray(bytes, bytes.length, (byte)47);
    }

    public static byte[][] bytes2byteArray(byte[] bytes, byte separator) {
        return bytes2byteArray(bytes, bytes.length, separator);
    }

    public static byte[][] bytes2byteArray(byte[] bytes, int len, byte separator) {
        Preconditions.checkPositionIndex(len, bytes.length);
        if (len == 0) {
            return new byte[][]{null};
        } else {
            int splits = 0;

            for(int i = 1; i < len; ++i) {
                if (bytes[i - 1] == separator && bytes[i] != separator) {
                    ++splits;
                }
            }

            if (splits == 0 && bytes[0] == separator) {
                return new byte[][]{null};
            } else {
                ++splits;
                byte[][] result = new byte[splits][];
                int nextIndex = 0;

                for(int i = 0; i < splits; ++i) {
                    int startIndex;
                    for(startIndex = nextIndex; nextIndex < len && bytes[nextIndex] != separator; ++nextIndex) {
                    }

                    result[i] = nextIndex > 0 ? Arrays.copyOfRange(bytes, startIndex, nextIndex) : DFSUtilClient.EMPTY_BYTES;

                    do {
                        ++nextIndex;
                    } while(nextIndex < len && bytes[nextIndex] == separator);
                }

                return result;
            }
        }
    }

    public static String addKeySuffixes(String key, String... suffixes) {
        String keySuffix = DFSUtilClient.concatSuffixes(suffixes);
        return DFSUtilClient.addSuffix(key, keySuffix);
    }

    public static Map<String, InetSocketAddress> getRpcAddressesForNameserviceId(Configuration conf, String nsId, String defaultValue) {
        return DFSUtilClient.getAddressesForNameserviceId(conf, nsId, defaultValue, new String[]{"dfs.namenode.rpc-address"});
    }

    public static Set<String> getAllNnPrincipals(Configuration conf) throws IOException {
        Set<String> principals = new HashSet();
        Iterator var2 = DFSUtilClient.getNameServiceIds(conf).iterator();

        while(true) {
            while(var2.hasNext()) {
                String nsId = (String)var2.next();
                String nnId;
                if (HAUtil.isHAEnabled(conf, nsId)) {
                    Iterator var8 = DFSUtilClient.getNameNodeIds(conf, nsId).iterator();

                    while(var8.hasNext()) {
                        nnId = (String)var8.next();
                        Configuration confForNn = new Configuration(conf);
                        NameNode.initializeGenericKeys(confForNn, nsId, nnId);
                        String principal = SecurityUtil.getServerPrincipal(confForNn.get("dfs.namenode.kerberos.principal"), DFSUtilClient.getNNAddress(confForNn).getHostName());
                        principals.add(principal);
                    }
                } else {
                    Configuration confForNn = new Configuration(conf);
                    NameNode.initializeGenericKeys(confForNn, nsId, (String)null);
                    nnId = SecurityUtil.getServerPrincipal(confForNn.get("dfs.namenode.kerberos.principal"), DFSUtilClient.getNNAddress(confForNn).getHostName());
                    principals.add(nnId);
                }
            }

            return principals;
        }
    }

    public static Map<String, Map<String, InetSocketAddress>> getBackupNodeAddresses(Configuration conf) throws IOException {
        Map<String, Map<String, InetSocketAddress>> addressList = DFSUtilClient.getAddresses(conf, (String)null, new String[]{"dfs.namenode.backup.address"});
        if (addressList.isEmpty()) {
            throw new IOException("Incorrect configuration: backup node address dfs.namenode.backup.address is not configured.");
        } else {
            return addressList;
        }
    }

    public static Map<String, Map<String, InetSocketAddress>> getSecondaryNameNodeAddresses(Configuration conf) throws IOException {
        Map<String, Map<String, InetSocketAddress>> addressList = DFSUtilClient.getAddresses(conf, (String)null, new String[]{"dfs.namenode.secondary.http-address"});
        if (addressList.isEmpty()) {
            throw new IOException("Incorrect configuration: secondary namenode address dfs.namenode.secondary.http-address is not configured.");
        } else {
            return addressList;
        }
    }

    public static Map<String, Map<String, InetSocketAddress>> getNNServiceRpcAddresses(Configuration conf) throws IOException {
        String defaultAddress;
        try {
            defaultAddress = NetUtils.getHostPortString(DFSUtilClient.getNNAddress(conf));
        } catch (IllegalArgumentException var3) {
            defaultAddress = null;
        }

        Map<String, Map<String, InetSocketAddress>> addressList = DFSUtilClient.getAddresses(conf, defaultAddress, new String[]{"dfs.namenode.servicerpc-address", "dfs.namenode.rpc-address"});
        if (addressList.isEmpty()) {
            throw new IOException("Incorrect configuration: namenode address dfs.namenode.servicerpc-address or dfs.namenode.rpc-address is not configured.");
        } else {
            return addressList;
        }
    }

    public static Map<String, Map<String, InetSocketAddress>> getNNServiceRpcAddressesForCluster(Configuration conf) throws IOException {
        String defaultAddress;
        try {
            defaultAddress = NetUtils.getHostPortString(DFSUtilClient.getNNAddress(conf));
        } catch (IllegalArgumentException var6) {
            defaultAddress = null;
        }

        Collection<String> parentNameServices = conf.getTrimmedStringCollection("dfs.internal.nameservices");
        if (parentNameServices.isEmpty()) {
            parentNameServices = conf.getTrimmedStringCollection("dfs.nameservices");
        } else {
            Set<String> availableNameServices = Sets.newHashSet(conf.getTrimmedStringCollection("dfs.nameservices"));
            Iterator var4 = parentNameServices.iterator();

            while(var4.hasNext()) {
                String nsId = (String)var4.next();
                if (!availableNameServices.contains(nsId)) {
                    throw new IOException("Unknown nameservice: " + nsId);
                }
            }
        }

        Map<String, Map<String, InetSocketAddress>> addressList = DFSUtilClient.getAddressesForNsIds(conf, parentNameServices, defaultAddress, new String[]{"dfs.namenode.servicerpc-address", "dfs.namenode.rpc-address"});
        if (addressList.isEmpty()) {
            throw new IOException("Incorrect configuration: namenode address dfs.namenode.servicerpc-address or dfs.namenode.rpc-address is not configured.");
        } else {
            return addressList;
        }
    }

    public static Map<String, Map<String, InetSocketAddress>> getNNLifelineRpcAddressesForCluster(Configuration conf) throws IOException {
        Collection<String> parentNameServices = conf.getTrimmedStringCollection("dfs.internal.nameservices");
        if (parentNameServices.isEmpty()) {
            parentNameServices = conf.getTrimmedStringCollection("dfs.nameservices");
        } else {
            Set<String> availableNameServices = Sets.newHashSet(conf.getTrimmedStringCollection("dfs.nameservices"));
            Iterator var3 = parentNameServices.iterator();

            while(var3.hasNext()) {
                String nsId = (String)var3.next();
                if (!availableNameServices.contains(nsId)) {
                    throw new IOException("Unknown nameservice: " + nsId);
                }
            }
        }

        return DFSUtilClient.getAddressesForNsIds(conf, parentNameServices, (String)null, new String[]{"dfs.namenode.lifeline.rpc-address"});
    }

    public static String getNamenodeLifelineAddr(Configuration conf, String nsId, String nnId) {
        if (nsId == null) {
            nsId = getOnlyNameServiceIdOrNull(conf);
        }

        String lifelineAddrKey = DFSUtilClient.concatSuffixes(new String[]{"dfs.namenode.lifeline.rpc-address", nsId, nnId});
        return conf.get(lifelineAddrKey);
    }

    public static List<DFSUtil.ConfiguredNNAddress> flattenAddressMap(Map<String, Map<String, InetSocketAddress>> map) {
        List<DFSUtil.ConfiguredNNAddress> ret = Lists.newArrayList();
        Iterator var2 = map.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, Map<String, InetSocketAddress>> entry = (Entry)var2.next();
            String nsId = (String)entry.getKey();
            Map<String, InetSocketAddress> nnMap = (Map)entry.getValue();
            Iterator var6 = nnMap.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<String, InetSocketAddress> e2 = (Entry)var6.next();
                String nnId = (String)e2.getKey();
                InetSocketAddress addr = (InetSocketAddress)e2.getValue();
                ret.add(new DFSUtil.ConfiguredNNAddress(nsId, nnId, addr));
            }
        }

        return ret;
    }

    public static String addressMapToString(Map<String, Map<String, InetSocketAddress>> map) {
        StringBuilder b = new StringBuilder();
        Iterator var2 = map.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, Map<String, InetSocketAddress>> entry = (Entry)var2.next();
            String nsId = (String)entry.getKey();
            Map<String, InetSocketAddress> nnMap = (Map)entry.getValue();
            b.append("Nameservice <").append(nsId).append(">:").append("\n");
            Iterator var6 = nnMap.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<String, InetSocketAddress> e2 = (Entry)var6.next();
                b.append("  NN ID ").append((String)e2.getKey()).append(" => ").append(e2.getValue()).append("\n");
            }
        }

        return b.toString();
    }

    public static String nnAddressesAsString(Configuration conf) {
        Map<String, Map<String, InetSocketAddress>> addresses = DFSUtilClient.getHaNnRpcAddresses(conf);
        return addressMapToString(addresses);
    }

    static Collection<String> getInternalNameServices(Configuration conf) {
        Collection<String> ids = conf.getTrimmedStringCollection("dfs.internal.nameservices");
        return !ids.isEmpty() ? ids : DFSUtilClient.getNameServiceIds(conf);
    }

    public static Collection<URI> getInternalNsRpcUris(Configuration conf) {
        return getNameServiceUris(conf, getInternalNameServices(conf), "dfs.namenode.servicerpc-address", "dfs.namenode.rpc-address");
    }

    static Collection<URI> getNameServiceUris(Configuration conf, Collection<String> nameServices, String... keys) {
        Set<URI> ret = new HashSet();
        Set<URI> nonPreferredUris = new HashSet();
        Iterator var5 = nameServices.iterator();

        while(var5.hasNext()) {
            String nsId = (String)var5.next();
            URI nsUri = createUri("hdfs", nsId, -1);
            boolean useLogicalUri = false;

            try {
                useLogicalUri = HAUtil.useLogicalUri(conf, nsUri);
            } catch (IOException var16) {
                LOG.warn("Getting exception  while trying to determine if nameservice " + nsId + " can use logical URI: " + var16);
            }

            if (HAUtil.isHAEnabled(conf, nsId) && useLogicalUri) {
                ret.add(nsUri);
            } else {
                boolean uriFound = false;
                String[] var10 = keys;
                int var11 = keys.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    String key = var10[var12];
                    String addr = conf.get(DFSUtilClient.concatSuffixes(new String[]{key, nsId}));
                    if (addr != null) {
                        URI uri = createUri("hdfs", NetUtils.createSocketAddr(addr));
                        if (!uriFound) {
                            uriFound = true;
                            ret.add(uri);
                        } else {
                            nonPreferredUris.add(uri);
                        }
                    }
                }
            }
        }

        boolean uriFound = false;
        String[] var18 = keys;
        int var20 = keys.length;

        for(int var21 = 0; var21 < var20; ++var21) {
            String key = var18[var21];
            String addr = conf.get(key);
            if (addr != null) {
                URI uri = createUri("hdfs", NetUtils.createSocketAddr(addr));
                if (!uriFound) {
                    uriFound = true;
                    ret.add(uri);
                } else {
                    nonPreferredUris.add(uri);
                }
            }
        }

        if (!uriFound) {
            URI defaultUri = FileSystem.getDefaultUri(conf);
            if (defaultUri != null) {
                if (defaultUri.getPort() != -1) {
                    defaultUri = createUri(defaultUri.getScheme(), NetUtils.createSocketAddr(defaultUri.getHost(), defaultUri.getPort()));
                }

                defaultUri = trimUri(defaultUri);
                if ("hdfs".equals(defaultUri.getScheme()) && !nonPreferredUris.contains(defaultUri)) {
                    ret.add(defaultUri);
                }
            }
        }

        return ret;
    }

    public static String getNameServiceIdFromAddress(Configuration conf, InetSocketAddress address, String... keys) {
        String[] ids = getSuffixIDs(conf, address, keys);
        return ids != null ? ids[0] : null;
    }

    public static URI getInfoServer(InetSocketAddress namenodeAddr, Configuration conf, String scheme) throws IOException {
        String[] suffixes = null;
        if (namenodeAddr != null) {
            suffixes = getSuffixIDs(conf, namenodeAddr, "dfs.namenode.servicerpc-address", "dfs.namenode.rpc-address");
        }

        String authority;
        if ("http".equals(scheme)) {
            authority = getSuffixedConf(conf, "dfs.namenode.http-address", "0.0.0.0:9870", suffixes);
        } else {
            if (!"https".equals(scheme)) {
                throw new IllegalArgumentException("Invalid scheme:" + scheme);
            }

            authority = getSuffixedConf(conf, "dfs.namenode.https-address", "0.0.0.0:9871", suffixes);
        }

        if (namenodeAddr != null) {
            authority = substituteForWildcardAddress(authority, namenodeAddr.getHostName());
        }

        return URI.create(scheme + "://" + authority);
    }

    public static URI getInfoServerWithDefaultHost(String defaultHost, Configuration conf, String scheme) throws IOException {
        URI configuredAddr = getInfoServer((InetSocketAddress)null, conf, scheme);
        String authority = substituteForWildcardAddress(configuredAddr.getAuthority(), defaultHost);
        return URI.create(scheme + "://" + authority);
    }

    public static String getHttpClientScheme(Configuration conf) {
        Policy policy = getHttpPolicy(conf);
        return policy == Policy.HTTPS_ONLY ? "https" : "http";
    }

    @VisibleForTesting
    static String substituteForWildcardAddress(String configuredAddress, String defaultHost) {
        InetSocketAddress sockAddr = NetUtils.createSocketAddr(configuredAddress);
        InetAddress addr = sockAddr.getAddress();
        return addr != null && addr.isAnyLocalAddress() ? defaultHost + ":" + sockAddr.getPort() : configuredAddress;
    }

    private static String getSuffixedConf(Configuration conf, String key, String defaultVal, String[] suffixes) {
        String ret = conf.get(addKeySuffixes(key, suffixes));
        return ret != null ? ret : conf.get(key, defaultVal);
    }

    public static void setGenericConf(Configuration conf, String nameserviceId, String nnId, String... keys) {
        String[] var4 = keys;
        int var5 = keys.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String key = var4[var6];
            String value = conf.get(addKeySuffixes(key, nameserviceId, nnId));
            if (value != null) {
                conf.set(key, value);
            } else {
                value = conf.get(addKeySuffixes(key, nameserviceId));
                if (value != null) {
                    conf.set(key, value);
                }
            }
        }

    }

    public static int roundBytesToGB(long bytes) {
        return Math.round((float)bytes / 1024.0F / 1024.0F / 1024.0F);
    }

    public static String getNamenodeNameServiceId(Configuration conf) {
        return getNameServiceId(conf, "dfs.namenode.rpc-address");
    }

    public static String getBackupNameServiceId(Configuration conf) {
        return getNameServiceId(conf, "dfs.namenode.backup.address");
    }

    public static String getSecondaryNameServiceId(Configuration conf) {
        return getNameServiceId(conf, "dfs.namenode.secondary.http-address");
    }

    private static String getNameServiceId(Configuration conf, String addressKey) {
        String nameserviceId = conf.get("dfs.nameservice.id");
        if (nameserviceId != null) {
            return nameserviceId;
        } else {
            Collection<String> nsIds = DFSUtilClient.getNameServiceIds(conf);
            if (1 == nsIds.size()) {
                return ((String[])nsIds.toArray(new String[1]))[0];
            } else {
                String nnId = conf.get("dfs.ha.namenode.id");
                return getSuffixIDs(conf, addressKey, (String)null, nnId, LOCAL_ADDRESS_MATCHER)[0];
            }
        }
    }

    static String[] getSuffixIDs(Configuration conf, String addressKey, String knownNsId, String knownNNId, DFSUtil.AddressMatcher matcher) {
        String nameserviceId = null;
        String namenodeId = null;
        int found = 0;
        Collection<String> nsIds = DFSUtilClient.getNameServiceIds(conf);
        Iterator var9 = DFSUtilClient.emptyAsSingletonNull(nsIds).iterator();

        label65:
        while(true) {
            String nsId;
            do {
                if (!var9.hasNext()) {
                    if (found > 1) {
                        String msg = "Configuration has multiple addresses that match local node's address. Please configure the system with dfs.nameservice.id and dfs.ha.namenode.id";
                        throw new HadoopIllegalArgumentException(msg);
                    }

                    return new String[]{nameserviceId, namenodeId};
                }

                nsId = (String)var9.next();
            } while(knownNsId != null && !knownNsId.equals(nsId));

            Collection<String> nnIds = DFSUtilClient.getNameNodeIds(conf, nsId);
            Iterator var12 = DFSUtilClient.emptyAsSingletonNull(nnIds).iterator();

            while(true) {
                String nnId;
                String addr;
                do {
                    do {
                        if (!var12.hasNext()) {
                            continue label65;
                        }

                        nnId = (String)var12.next();
                        if (LOG.isTraceEnabled()) {
                            LOG.trace(String.format("addressKey: %s nsId: %s nnId: %s", addressKey, nsId, nnId));
                        }
                    } while(knownNNId != null && !knownNNId.equals(nnId));

                    String key = addKeySuffixes(addressKey, nsId, nnId);
                    addr = conf.get(key);
                } while(addr == null);

                InetSocketAddress s = null;

                try {
                    s = NetUtils.createSocketAddr(addr);
                } catch (Exception var18) {
                    LOG.warn("Exception in creating socket address " + addr, var18);
                    continue;
                }

                if (!s.isUnresolved() && matcher.match(s)) {
                    nameserviceId = nsId;
                    namenodeId = nnId;
                    ++found;
                }
            }
        }
    }

    static String[] getSuffixIDs(Configuration conf, final InetSocketAddress address, String... keys) {
        DFSUtil.AddressMatcher matcher = new DFSUtil.AddressMatcher() {
            public boolean match(InetSocketAddress s) {
                return address.equals(s);
            }
        };
        String[] var4 = keys;
        int var5 = keys.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String key = var4[var6];
            String[] ids = getSuffixIDs(conf, key, (String)null, (String)null, matcher);
            if (ids != null && (ids[0] != null || ids[1] != null)) {
                return ids;
            }
        }

        return null;
    }

    public static URI createUri(String scheme, InetSocketAddress address) {
        return createUri(scheme, address.getHostName(), address.getPort());
    }

    public static URI createUri(String scheme, String host, int port) {
        try {
            return new URI(scheme, (String)null, host, port, (String)null, (String)null, (String)null);
        } catch (URISyntaxException var4) {
            throw new IllegalArgumentException(var4.getMessage(), var4);
        }
    }

    static URI trimUri(URI uri) {
        String path = uri.getPath();
        if ("hdfs".equals(uri.getScheme()) && path != null && !path.isEmpty()) {
            uri = createUri(uri.getScheme(), uri.getHost(), uri.getPort());
        }

        return uri;
    }

    public static void addPBProtocol(Configuration conf, Class<?> protocol, BlockingService service, Server server) throws IOException {
        RPC.setProtocolEngine(conf, protocol, ProtobufRpcEngine.class);
        server.addProtocol(RpcKind.RPC_PROTOCOL_BUFFER, protocol, service);
    }

    public static String getNamenodeServiceAddr(Configuration conf, String nsId, String nnId) {
        if (nsId == null) {
            nsId = getOnlyNameServiceIdOrNull(conf);
        }

        String serviceAddrKey = DFSUtilClient.concatSuffixes(new String[]{"dfs.namenode.servicerpc-address", nsId, nnId});
        String addrKey = DFSUtilClient.concatSuffixes(new String[]{"dfs.namenode.rpc-address", nsId, nnId});
        String serviceRpcAddr = conf.get(serviceAddrKey);
        if (serviceRpcAddr == null) {
            serviceRpcAddr = conf.get(addrKey);
        }

        return serviceRpcAddr;
    }

    public static String getNamenodeWebAddr(Configuration conf, String nsId, String nnId) {
        if (nsId == null) {
            nsId = getOnlyNameServiceIdOrNull(conf);
        }

        String webAddrKey = DFSUtilClient.concatSuffixes(new String[]{"dfs.namenode.http-address", nsId, nnId});
        String webAddr = conf.get(webAddrKey, "0.0.0.0:9870");
        return webAddr;
    }

    public static Map<String, InetSocketAddress> getWebAddressesForNameserviceId(Configuration conf, String nsId, String defaultValue) {
        return DFSUtilClient.getAddressesForNameserviceId(conf, nsId, defaultValue, new String[]{"dfs.namenode.http-address"});
    }

    public static String getOnlyNameServiceIdOrNull(Configuration conf) {
        Collection<String> nsIds = DFSUtilClient.getNameServiceIds(conf);
        return 1 == nsIds.size() ? ((String[])nsIds.toArray(new String[1]))[0] : null;
    }

    public static boolean parseHelpArgument(String[] args, String helpDescription, PrintStream out, boolean printGenericCommandUsage) {
        if (args.length == 1) {
            try {
                CommandLineParser parser = new PosixParser();
                CommandLine cmdLine = parser.parse(helpOptions, args);
                if (cmdLine.hasOption(helpOpt.getOpt()) || cmdLine.hasOption(helpOpt.getLongOpt())) {
                    out.println(helpDescription + "\n");
                    if (printGenericCommandUsage) {
                        ToolRunner.printGenericCommandUsage(out);
                    }

                    return true;
                }
            } catch (ParseException var6) {
                return false;
            }
        }

        return false;
    }

    public static float getInvalidateWorkPctPerIteration(Configuration conf) {
        float blocksInvalidateWorkPct = conf.getFloat("dfs.namenode.invalidate.work.pct.per.iteration", 0.32F);
        Preconditions.checkArgument(blocksInvalidateWorkPct > 0.0F && blocksInvalidateWorkPct <= 1.0F, "dfs.namenode.invalidate.work.pct.per.iteration = '" + blocksInvalidateWorkPct + "' is invalid. It should be a positive, non-zero float value, not greater than 1.0f, to indicate a percentage.");
        return blocksInvalidateWorkPct;
    }

    public static int getReplWorkMultiplier(Configuration conf) {
        int blocksReplWorkMultiplier = conf.getInt("dfs.namenode.replication.work.multiplier.per.iteration", 2);
        Preconditions.checkArgument(blocksReplWorkMultiplier > 0, "dfs.namenode.replication.work.multiplier.per.iteration = '" + blocksReplWorkMultiplier + "' is invalid. It should be a positive, non-zero integer value.");
        return blocksReplWorkMultiplier;
    }

    public static String getSpnegoKeytabKey(Configuration conf, String defaultKey) {
        String value = conf.get("dfs.web.authentication.kerberos.keytab");
        return value != null && !value.isEmpty() ? "dfs.web.authentication.kerberos.keytab" : defaultKey;
    }

    public static Policy getHttpPolicy(Configuration conf) {
        String policyStr = conf.get("dfs.http.policy", DFSConfigKeys.DFS_HTTP_POLICY_DEFAULT);
        Policy policy = Policy.fromString(policyStr);
        if (policy == null) {
            throw new HadoopIllegalArgumentException("Unregonized value '" + policyStr + "' for " + "dfs.http.policy");
        } else {
            conf.set("dfs.http.policy", policy.name());
            return policy;
        }
    }

    public static Builder loadSslConfToHttpServerBuilder(Builder builder, Configuration sslConf) {
        return builder.needsClientAuth(sslConf.getBoolean("dfs.client.https.need-auth", false)).keyPassword(getPassword(sslConf, "ssl.server.keystore.keypassword")).keyStore(sslConf.get("ssl.server.keystore.location"), getPassword(sslConf, "ssl.server.keystore.password"), sslConf.get("ssl.server.keystore.type", "jks")).trustStore(sslConf.get("ssl.server.truststore.location"), getPassword(sslConf, "ssl.server.truststore.password"), sslConf.get("ssl.server.truststore.type", "jks")).excludeCiphers(sslConf.get("ssl.server.exclude.cipher.list"));
    }

    static String getPassword(Configuration conf, String alias) {
        String password = null;

        try {
            char[] passchars = conf.getPassword(alias);
            if (passchars != null) {
                password = new String(passchars);
            }
        } catch (IOException var4) {
            LOG.warn("Setting password to null since IOException is caught when getting password", var4);
            password = null;
        }

        return password;
    }

    public static String dateToIso8601String(Date date) {
        return DFSUtilClient.dateToIso8601String(date);
    }

    public static String durationToString(long durationMs) {
        return DFSUtilClient.durationToString(durationMs);
    }

    public static long parseRelativeTime(String relTime) throws IOException {
        if (relTime.length() < 2) {
            throw new IOException("Unable to parse relative time value of " + relTime + ": too short");
        } else {
            String ttlString = relTime.substring(0, relTime.length() - 1);

            long ttl;
            try {
                ttl = Long.parseLong(ttlString);
            } catch (NumberFormatException var5) {
                throw new IOException("Unable to parse relative time value of " + relTime + ": " + ttlString + " is not a number");
            }

            if (!relTime.endsWith("s")) {
                if (relTime.endsWith("m")) {
                    ttl *= 60L;
                } else if (relTime.endsWith("h")) {
                    ttl *= 3600L;
                } else {
                    if (!relTime.endsWith("d")) {
                        throw new IOException("Unable to parse relative time value of " + relTime + ": unknown time unit " + relTime.charAt(relTime.length() - 1));
                    }

                    ttl *= 86400L;
                }
            }

            return ttl * 1000L;
        }
    }

    public static Configuration loadSslConfiguration(Configuration conf) {
        Configuration sslConf = new Configuration(false);
        sslConf.addResource(conf.get("dfs.https.server.keystore.resource", "ssl-server.xml"));
        String[] reqSslProps = new String[]{"ssl.server.truststore.location", "ssl.server.keystore.location", "ssl.server.keystore.password", "ssl.server.keystore.keypassword"};
        String[] var3 = reqSslProps;
        int var4 = reqSslProps.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String sslProp = var3[var5];
            if (sslConf.get(sslProp) == null) {
                LOG.warn("SSL config " + sslProp + " is missing. If " + "dfs.https.server.keystore.resource" + " is specified, make sure it is a relative path");
            }
        }

        boolean requireClientAuth = conf.getBoolean("dfs.client.https.need-auth", false);
        sslConf.setBoolean("dfs.client.https.need-auth", requireClientAuth);
        return sslConf;
    }

    public static Builder httpServerTemplateForNNAndJN(Configuration conf, InetSocketAddress httpAddr, InetSocketAddress httpsAddr, String name, String spnegoUserNameKey, String spnegoKeytabFileKey) throws IOException {
        Policy policy = getHttpPolicy(conf);
        Builder builder = (new Builder()).setName(name).setConf(conf).setACL(new AccessControlList(conf.get("dfs.cluster.administrators", " "))).setSecurityEnabled(UserGroupInformation.isSecurityEnabled()).setUsernameConfKey(spnegoUserNameKey).setKeytabConfKey(getSpnegoKeytabKey(conf, spnegoKeytabFileKey));
        if (UserGroupInformation.isSecurityEnabled()) {
            LOG.info("Starting web server as: " + SecurityUtil.getServerPrincipal(conf.get(spnegoUserNameKey), httpAddr.getHostName()));
        }

        if (policy.isHttpEnabled()) {
            if (httpAddr.getPort() == 0) {
                builder.setFindPort(true);
            }

            URI uri = URI.create("http://" + NetUtils.getHostPortString(httpAddr));
            builder.addEndpoint(uri);
            LOG.info("Starting Web-server for " + name + " at: " + uri);
        }

        if (policy.isHttpsEnabled() && httpsAddr != null) {
            Configuration sslConf = loadSslConfiguration(conf);
            loadSslConfToHttpServerBuilder(builder, sslConf);
            if (httpsAddr.getPort() == 0) {
                builder.setFindPort(true);
            }

            URI uri = URI.create("https://" + NetUtils.getHostPortString(httpsAddr));
            builder.addEndpoint(uri);
            LOG.info("Starting Web-server for " + name + " at: " + uri);
        }

        return builder;
    }

    public static void assertAllResultsEqual(Collection<?> objects) throws AssertionError {
        if (objects.size() != 0 && objects.size() != 1) {
            Object[] resultsArray = objects.toArray();

            for(int i = 1; i < resultsArray.length; ++i) {
                Object currElement = resultsArray[i];
                Object lastElement = resultsArray[i - 1];
                if (currElement == null && currElement != lastElement || currElement != null && !currElement.equals(lastElement)) {
                    throw new AssertionError("Not all elements match in results: " + Arrays.toString(resultsArray));
                }
            }

        }
    }

    public static KeyProviderCryptoExtension createKeyProviderCryptoExtension(Configuration conf) throws IOException {
        KeyProvider keyProvider = null;//DFSUtilClient.createKeyProvider(conf);
        if (keyProvider == null) {
            return null;
        } else {
            KeyProviderCryptoExtension cryptoProvider = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(keyProvider);
            return cryptoProvider;
        }
    }

    public static DelegationTokenIdentifier decodeDelegationToken(Token<DelegationTokenIdentifier> token) throws IOException {
        DelegationTokenIdentifier id = new DelegationTokenIdentifier();
        ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        DataInputStream in = new DataInputStream(buf);
        Throwable var4 = null;

        try {
            id.readFields(in);
        } catch (Throwable var13) {
            var4 = var13;
            throw var13;
        } finally {
            if (in != null) {
                if (var4 != null) {
                    try {
                        in.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    in.close();
                }
            }

        }

        return id;
    }

    static {
        helpOptions.addOption(helpOpt);
    }

    private interface AddressMatcher {
        boolean match(InetSocketAddress var1);
    }

    public static class ConfiguredNNAddress {
        private final String nameserviceId;
        private final String namenodeId;
        private final InetSocketAddress addr;

        private ConfiguredNNAddress(String nameserviceId, String namenodeId, InetSocketAddress addr) {
            this.nameserviceId = nameserviceId;
            this.namenodeId = namenodeId;
            this.addr = addr;
        }

        public String getNameserviceId() {
            return this.nameserviceId;
        }

        public String getNamenodeId() {
            return this.namenodeId;
        }

        public InetSocketAddress getAddress() {
            return this.addr;
        }

        public String toString() {
            return "ConfiguredNNAddress[nsId=" + this.nameserviceId + ";nnId=" + this.namenodeId + ";addr=" + this.addr + "]";
        }
    }

    @Private
    public static class ServiceAndStaleComparator extends DFSUtil.ServiceComparator {
        private final long staleInterval;

        public ServiceAndStaleComparator(long interval) {
            this.staleInterval = interval;
        }

        public int compare(DatanodeInfo a, DatanodeInfo b) {
            int ret = super.compare(a, b);
            if (ret != 0) {
                return ret;
            } else {
                boolean aStale = a.isStale(this.staleInterval);
                boolean bStale = b.isStale(this.staleInterval);
                return aStale == bStale ? 0 : (aStale ? 1 : -1);
            }
        }
    }

    public static class ServiceComparator implements Comparator<DatanodeInfo> {
        public ServiceComparator() {
        }

        public int compare(DatanodeInfo a, DatanodeInfo b) {
            if (a.isDecommissioned()) {
                return b.isDecommissioned() ? 0 : 1;
            } else if (b.isDecommissioned()) {
                return -1;
            } else if (a.isEnteringMaintenance()) {
                return b.isEnteringMaintenance() ? 0 : 1;
            } else {
                return b.isEnteringMaintenance() ? -1 : 0;
            }
        }
    }
}
