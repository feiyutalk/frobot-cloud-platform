package org.iceslab.frobot.cluster;

public enum ProjectReturnCode {

    PROJECTSTATUS_WAIT(601),

    PROJECTSTATUS_RUN(602),

    PROJECTSTATUS_STOP(600),

    PROJECTSTATUS_FINISH(603),

    PROJECTSTATUS_DELETE(604),

    PROJECTSTATUS_DISCONNECTION(605),

    PROJECTSTATUS_PAUSE(606),

    /*=================Project Deploy Return Code =============*/
    PROJECTDEPLOY_AREDYEXIXT(200),

    PROJECTDEPLOY_EXITEDPROJECT(201),

    PROJECTDEPLOY_FAIL(202),

    PROJECTDEPLOY_SUCCESS(203),
    /*=================Project Deploy Return Code =============*/
    PROJECTDIVIDE_SUCCESS(800),

    PROJECTDIVIDE_FAILURE(801),
	
	PROJECTSAVE_FAILURE(802);

    private final int code;

    private ProjectReturnCode(int code) {
        this.code = code;
    }

    public static ProjectReturnCode valueOf(int code) {
        for (ProjectReturnCode projectReturnCode : ProjectReturnCode.values()) {
            if (projectReturnCode.code == code) {
                return projectReturnCode;
            }
        }
        throw new IllegalArgumentException("can't find the project return code !");
    }

    public int code() {
        return this.code;
    }
}
