package com.djimgou.session.geolocation;

import javax.servlet.http.HttpServletRequest;

/*@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 101)*/
public class SessionDetailsFilter /*extends OncePerRequestFilter*/ {
    //@Autowired
    //FindByIndexNameSessionRepository<? extends Session> sessions;
    /*static final String UNKNOWN = "Unknown";

    private DatabaseReader reader;

    @Autowired
    public SessionDetailsFilter(DatabaseReader reader) {
        this.reader = reader;
    }

    // tag::dofilterinternal[]
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);

        HttpSession session = request.getSession(false);
        if (session != null) {
            String remoteAddr = getRemoteAddress(request);
            String geoLocation = getGeoLocation(remoteAddr);

            SessionDetails details = new SessionDetails();
            details.setAccessType(request.getHeader("User-Agent"));
            details.setLocation(remoteAddr + " " + geoLocation);

            session.setAttribute("SESSION_DETAILS", details);
        }
    }
    // end::dofilterinternal[]

    String getGeoLocation(String remoteAddr) {
        try {
            CityResponse city = this.reader.city(InetAddress.getByName(remoteAddr));
            String cityName = city.getCity().getName();
            String countryName = city.getCountry().getName();
            if (cityName == null && countryName == null) {
                return null;
            }
            else if (cityName == null) {
                return countryName;
            }
            else if (countryName == null) {
                return cityName;
            }
            return cityName + ", " + countryName;
        }
        catch (Exception ex) {
            return UNKNOWN;

        }
    }*/

    private String getRemoteAddress(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null) {
            remoteAddr = request.getRemoteAddr();
        }
        else if (remoteAddr.contains(",")) {
            remoteAddr = remoteAddr.split(",")[0];
        }
        return remoteAddr;
    }
}
