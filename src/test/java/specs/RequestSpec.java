package specs;

import config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RequestSpec {

    private RequestSpec() {
    }

    public static RequestSpecification getRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(Config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.URI);

        RequestSpecification requestSpecification = builder.build();
        if (Config.isRelaxedHttpsValidationEnabled()) {
            requestSpecification.relaxedHTTPSValidation();
        }
        return requestSpecification;
    }
}
