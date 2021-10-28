package com.act.session.geolocation;
import java.io.Serializable;

public class SessionDetails  implements Serializable {

        private String location;

        private String accessType;

        public String getLocation() {
            return this.location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getAccessType() {
            return this.accessType;
        }

        public void setAccessType(String accessType) {
            this.accessType = accessType;
        }

        private static final long serialVersionUID = 8850489178248613501L;
}
