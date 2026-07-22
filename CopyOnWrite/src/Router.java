/**
 * 路由信息
 * Router
 */
public final class Router {
    private final String ip;
    private final Integer port;
    private final String iface;
    public Router(String ip, Integer port, String iface) {
        this.ip = ip;
        this.port = port;
        this.iface = iface;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((iface == null) ? 0 : iface.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Router other = (Router) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (iface == null) {
            if (other.iface != null)
                return false;
        } else if (!iface.equals(other.iface))
            return false;
        return true;
    }
    public String getIp() {
        return ip;
    }
    public Integer getPort() {
        return port;
    }
    public String getIface() {
        return iface;
    }
}
