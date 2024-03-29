package opensaml.v3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

public class SAMLParser {

	public static void main(String ars[])
			throws InitializationException, ComponentInitializationException, XMLParserException, UnmarshallingException, CertificateException, ValidationException, SignatureException {

		String base64EncodedSAML = "PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iIElEPSJfOGU4ZGM1ZjY5YTk4Y2M0YzFmZjM0MjdlNWNlMzQ2MDZmZDY3MmY5MWU2IiBWZXJzaW9uPSIyLjAiIElzc3VlSW5zdGFudD0iMjAxNC0wNy0xN1QwMTowMTo0OFoiIERlc3RpbmF0aW9uPSJodHRwOi8vc3AuZXhhbXBsZS5jb20vZGVtbzEvaW5kZXgucGhwP2FjcyIgSW5SZXNwb25zZVRvPSJPTkVMT0dJTl80ZmVlM2IwNDYzOTVjNGU3NTEwMTFlOTdmODkwMGI1MjczZDU2Njg1Ij4NCiAgPHNhbWw6SXNzdWVyPmh0dHA6Ly9pZHAuZXhhbXBsZS5jb20vbWV0YWRhdGEucGhwPC9zYW1sOklzc3Vlcj4NCiAgPHNhbWxwOlN0YXR1cz4NCiAgICA8c2FtbHA6U3RhdHVzQ29kZSBWYWx1ZT0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnN0YXR1czpTdWNjZXNzIi8+DQogIDwvc2FtbHA6U3RhdHVzPg0KICA8c2FtbDpBc3NlcnRpb24geG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeG1sbnM6eHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hIiBJRD0icGZ4OTIzMDkyZjMtZWI1OS1kMjYzLTU5ZmQtZDcwYTY5YmE5N2E2IiBWZXJzaW9uPSIyLjAiIElzc3VlSW5zdGFudD0iMjAxNC0wNy0xN1QwMTowMTo0OFoiPg0KICAgIDxzYW1sOklzc3Vlcj5odHRwOi8vaWRwLmV4YW1wbGUuY29tL21ldGFkYXRhLnBocDwvc2FtbDpJc3N1ZXI+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+DQogIDxkczpTaWduZWRJbmZvPjxkczpDYW5vbmljYWxpemF0aW9uTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8+DQogICAgPGRzOlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIvPg0KICA8ZHM6UmVmZXJlbmNlIFVSST0iI3BmeDkyMzA5MmYzLWViNTktZDI2My01OWZkLWQ3MGE2OWJhOTdhNiI+PGRzOlRyYW5zZm9ybXM+PGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIi8+PGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjwvZHM6VHJhbnNmb3Jtcz48ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiLz48ZHM6RGlnZXN0VmFsdWU+SmU3eHFrRXJ4bkN4dWwvVjlIMnpPdk9kY21JPTwvZHM6RGlnZXN0VmFsdWU+PC9kczpSZWZlcmVuY2U+PC9kczpTaWduZWRJbmZvPjxkczpTaWduYXR1cmVWYWx1ZT53eVdSQ284bFFidStSMmd2OENRUzZWMzdKZittZytWZkhURlczNXRTZlVlQnFkTHBtdWNmTGVnc1loZXJLWWlBZUZmMnFXek1mYXFRZ0o1L3dRSkc4RUdFUkI4RkVBMEdBOFRaSVdtbytQU0N4R1BOTGo3TDVRL3ptQk90MEZ2Mm01SHd0UlllZmlETE82NXNLTW1rV0hEL2NKS0RCbjM5TGk1dDgrVEZoVVk9PC9kczpTaWduYXR1cmVWYWx1ZT4NCjxkczpLZXlJbmZvPjxkczpYNTA5RGF0YT48ZHM6WDUwOUNlcnRpZmljYXRlPk1JSUNhakNDQWRPZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRMEZBREJTTVFzd0NRWURWUVFHRXdKMWN6RVRNQkVHQTFVRUNBd0tRMkZzYVdadmNtNXBZVEVWTUJNR0ExVUVDZ3dNVDI1bGJHOW5hVzRnU1c1ak1SY3dGUVlEVlFRRERBNXpjQzVsZUdGdGNHeGxMbU52YlRBZUZ3MHhOREEzTVRjeE5ERXlOVFphRncweE5UQTNNVGN4TkRFeU5UWmFNRkl4Q3pBSkJnTlZCQVlUQW5Wek1STXdFUVlEVlFRSURBcERZV3hwWm05eWJtbGhNUlV3RXdZRFZRUUtEQXhQYm1Wc2IyZHBiaUJKYm1NeEZ6QVZCZ05WQkFNTURuTndMbVY0WVcxd2JHVXVZMjl0TUlHZk1BMEdDU3FHU0liM0RRRUJBUVVBQTRHTkFEQ0JpUUtCZ1FEWngrT040SVVvSVd4Z3VrVGIxdE9pWDNiTVl6WVFpd1dQVU5NcCtGcTgyeG9Ob2dzbzJieWtaRzB5aUptNW84enYvc2Q2cEdvdWF5TWdreC8yRlNPZGMzNlQwakdiQ0h1UlNidGlhMFBFek5JUnRtVmlNcnQzQWVvV0JpZFJYbVpzeENOTHdnSVY2ZG4yV3B1RTVBejBiSGdwWm5ReFRLRmVrMEJNS1UvZDh3SURBUUFCbzFBd1RqQWRCZ05WSFE0RUZnUVVHSHhZcVpZeVg3Y1R4S1ZPRFZnWndTVGRDbnd3SHdZRFZSMGpCQmd3Rm9BVUdIeFlxWll5WDdjVHhLVk9EVmdad1NUZENud3dEQVlEVlIwVEJBVXdBd0VCL3pBTkJna3Foa2lHOXcwQkFRMEZBQU9CZ1FCeUZPbCtoTUZJQ2JkM0RKZm5wMlJnZC9kcXR0c1pHL3R5aElMV3ZFcmJpby9ERWU5OG1YcG93aFRrQzA0RU5wck95WGk3WmJVcWlpY0Y4OXVBR3l0MW9xZ1RVQ0QxVnNMYWhxSWNtcnpndW1OeVR3TEdXbzE3V0RBYTEvdXNEaGV0V0FNaGd6Ri9DbmY1ZWswbkswMG0wWVpHeWM0THpnRDBDUk9NQVNUV05nPT08L2RzOlg1MDlDZXJ0aWZpY2F0ZT48L2RzOlg1MDlEYXRhPjwvZHM6S2V5SW5mbz48L2RzOlNpZ25hdHVyZT4NCiAgICA8c2FtbDpTdWJqZWN0Pg0KICAgICAgPHNhbWw6TmFtZUlEIFNQTmFtZVF1YWxpZmllcj0iaHR0cDovL3NwLmV4YW1wbGUuY29tL2RlbW8xL21ldGFkYXRhLnBocCIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDp0cmFuc2llbnQiPl9jZTNkMjk0OGI0Y2YyMDE0NmRlZTBhMGIzZGQ2ZjY5YjZjZjg2ZjYyZDc8L3NhbWw6TmFtZUlEPg0KICAgICAgPHNhbWw6U3ViamVjdENvbmZpcm1hdGlvbiBNZXRob2Q9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpjbTpiZWFyZXIiPg0KICAgICAgICA8c2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uRGF0YSBOb3RPbk9yQWZ0ZXI9IjIwMjQtMDEtMThUMDY6MjE6NDhaIiBSZWNpcGllbnQ9Imh0dHA6Ly9zcC5leGFtcGxlLmNvbS9kZW1vMS9pbmRleC5waHA/YWNzIiBJblJlc3BvbnNlVG89Ik9ORUxPR0lOXzRmZWUzYjA0NjM5NWM0ZTc1MTAxMWU5N2Y4OTAwYjUyNzNkNTY2ODUiLz4NCiAgICAgIDwvc2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uPg0KICAgIDwvc2FtbDpTdWJqZWN0Pg0KICAgIDxzYW1sOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE0LTA3LTE3VDAxOjAxOjE4WiIgTm90T25PckFmdGVyPSIyMDI0LTAxLTE4VDA2OjIxOjQ4WiI+DQogICAgICA8c2FtbDpBdWRpZW5jZVJlc3RyaWN0aW9uPg0KICAgICAgICA8c2FtbDpBdWRpZW5jZT5odHRwOi8vc3AuZXhhbXBsZS5jb20vZGVtbzEvbWV0YWRhdGEucGhwPC9zYW1sOkF1ZGllbmNlPg0KICAgICAgPC9zYW1sOkF1ZGllbmNlUmVzdHJpY3Rpb24+DQogICAgPC9zYW1sOkNvbmRpdGlvbnM+DQogICAgPHNhbWw6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0YW50PSIyMDE0LTA3LTE3VDAxOjAxOjQ4WiIgU2Vzc2lvbk5vdE9uT3JBZnRlcj0iMjAyNC0wNy0xN1QwOTowMTo0OFoiIFNlc3Npb25JbmRleD0iX2JlOTk2N2FiZDkwNGRkY2FlM2MwZWI0MTg5YWRiZTNmNzFlMzI3Y2Y5MyI+DQogICAgICA8c2FtbDpBdXRobkNvbnRleHQ+DQogICAgICAgIDxzYW1sOkF1dGhuQ29udGV4dENsYXNzUmVmPnVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkPC9zYW1sOkF1dGhuQ29udGV4dENsYXNzUmVmPg0KICAgICAgPC9zYW1sOkF1dGhuQ29udGV4dD4NCiAgICA8L3NhbWw6QXV0aG5TdGF0ZW1lbnQ+DQogICAgPHNhbWw6QXR0cmlidXRlU3RhdGVtZW50Pg0KICAgICAgPHNhbWw6QXR0cmlidXRlIE5hbWU9InVpZCIgTmFtZUZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmF0dHJuYW1lLWZvcm1hdDpiYXNpYyI+DQogICAgICAgIDxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhzaTp0eXBlPSJ4czpzdHJpbmciPnRlc3Q8L3NhbWw6QXR0cmlidXRlVmFsdWU+DQogICAgICA8L3NhbWw6QXR0cmlidXRlPg0KICAgICAgPHNhbWw6QXR0cmlidXRlIE5hbWU9Im1haWwiIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6YmFzaWMiPg0KICAgICAgICA8c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj50ZXN0QGV4YW1wbGUuY29tPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPg0KICAgICAgPC9zYW1sOkF0dHJpYnV0ZT4NCiAgICAgIDxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJlZHVQZXJzb25BZmZpbGlhdGlvbiIgTmFtZUZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmF0dHJuYW1lLWZvcm1hdDpiYXNpYyI+DQogICAgICAgIDxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhzaTp0eXBlPSJ4czpzdHJpbmciPnVzZXJzPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPg0KICAgICAgICA8c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj5leGFtcGxlcm9sZTE8L3NhbWw6QXR0cmlidXRlVmFsdWU+DQogICAgICA8L3NhbWw6QXR0cmlidXRlPg0KICAgIDwvc2FtbDpBdHRyaWJ1dGVTdGF0ZW1lbnQ+DQogIDwvc2FtbDpBc3NlcnRpb24+DQo8L3NhbWxwOlJlc3BvbnNlPg==";
	  	//String base64EncodedSAML =	"PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iIElEPSJfOGU4ZGM1ZjY5YTk4Y2M0YzFmZjM0MjdlNWNlMzQ2MDZmZDY3MmY5MWU2IiBWZXJzaW9uPSIyLjAiIElzc3VlSW5zdGFudD0iMjAxNC0wNy0xN1QwMTowMTo0OFoiIERlc3RpbmF0aW9uPSJodHRwOi8vc3AuZXhhbXBsZS5jb20vZGVtbzEvaW5kZXgucGhwP2FjcyIgSW5SZXNwb25zZVRvPSJPTkVMT0dJTl80ZmVlM2IwNDYzOTVjNGU3NTEwMTFlOTdmODkwMGI1MjczZDU2Njg1Ij4NCiAgPHNhbWw6SXNzdWVyPmh0dHA6Ly9pZHAuZXhhbXBsZS5jb20vbWV0YWRhdGEucGhwPC9zYW1sOklzc3Vlcj4NCiAgPHNhbWxwOlN0YXR1cz4NCiAgICA8c2FtbHA6U3RhdHVzQ29kZSBWYWx1ZT0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnN0YXR1czpTdWNjZXNzIi8+DQogIDwvc2FtbHA6U3RhdHVzPg0KICA8c2FtbDpBc3NlcnRpb24geG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeG1sbnM6eHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hIiBJRD0icGZ4OTIzMDkyZjMtZWI1OS1kMjYzLTU5ZmQtZDcwYTY5YmE5N2E2IiBWZXJzaW9uPSIyLjAiIElzc3VlSW5zdGFudD0iMjAxNC0wNy0xN1QwMTowMTo0OFoiPg0KICAgIDxzYW1sOklzc3Vlcj5odHRwOi8vaWRwLmV4YW1wbGUuY29tL21ldGFkYXRhLnBocDwvc2FtbDpJc3N1ZXI+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+DQogIDxkczpTaWduZWRJbmZvPjxkczpDYW5vbmljYWxpemF0aW9uTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8+DQogICAgPGRzOlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIvPg0KICA8ZHM6UmVmZXJlbmNlIFVSST0iI3BmeDkyMzA5MmYzLWViNTktZDI2My01OWZkLWQ3MGE2OWJhOTdhNiI+PGRzOlRyYW5zZm9ybXM+PGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIi8+PGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjwvZHM6VHJhbnNmb3Jtcz48ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiLz48ZHM6RGlnZXN0VmFsdWU+SmU3eHFrRXJ4bkN4dWwvVjlIMnpPdk9kY21JPTwvZHM6RGlnZXN0VmFsdWU+PC9kczpSZWZlcmVuY2U+PC9kczpTaWduZWRJbmZvPjxkczpTaWduYXR1cmVWYWx1ZT53eVdSQ284bFFidStSMmd2OENRUzZWMzdKZittZytWZkhURlczNXRTZlVlQnFkTHBtdWNmTGVnc1loZXJLWWlBZUZmMnFXek1mYXFRZ0o1L3dRSkc4RUdFUkI4RkVBMEdBOFRaSVdtbytQU0N4R1BOTGo3TDVRL3ptQk90MEZ2Mm01SHd0UlllZmlETE82NXNLTW1rV0hEL2NKS0RCbjM5TGk1dDgrVEZoVVk9PC9kczpTaWduYXR1cmVWYWx1ZT4NCjxkczpLZXlJbmZvPjxkczpYNTA5RGF0YT48ZHM6WDUwOUNlcnRpZmljYXRlPk1JSUNhakNDQWRPZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRMEZBREJTTVFzd0NRWURWUVFHRXdKMWN6RVRNQkVHQTFVRUNBd0tRMkZzYVdadmNtNXBZVEVWTUJNR0ExVUVDZ3dNVDI1bGJHOW5hVzRnU1c1ak1SY3dGUVlEVlFRRERBNXpjQzVsZUdGdGNHeGxMbU52YlRBZUZ3MHhOREEzTVRjeE5ERXlOVFphRncweE5UQTNNVGN4TkRFeU5UWmFNRkl4Q3pBSkJnTlZCQVlUQW5Wek1STXdFUVlEVlFRSURBcERZV3hwWm05eWJtbGhNUlV3RXdZRFZRUUtEQXhQYm1Wc2IyZHBiaUJKYm1NeEZ6QVZCZ05WQkFNTURuTndMbVY0WVcxd2JHVXVZMjl0TUlHZk1BMEdDU3FHU0liM0RRRUJBUVVBQTRHTkFEQ0JpUUtCZ1FEWngrT040SVVvSVd4Z3VrVGIxdE9pWDNiTVl6WVFpd1dQVU5NcCtGcTgyeG9Ob2dzbzJieWtaRzB5aUptNW84enYvc2Q2cEdvdWF5TWdreC8yRlNPZGMzNlQwakdiQ0h1UlNidGlhMFBFek5JUnRtVmlNcnQzQWVvV0JpZFJYbVpzeENOTHdnSVY2ZG4yV3B1RTVBejBiSGdwWm5ReFRLRmVrMEJNS1UvZDh3SURBUUFCbzFBd1RqQWRCZ05WSFE0RUZnUVVHSHhZcVpZeVg3Y1R4S1ZPRFZnWndTVGRDbnd3SHdZRFZSMGpCQmd3Rm9BVUdIeFlxWll5WDdjVHhLVk9EVmdad1NUZENud3dEQVlEVlIwVEJBVXdBd0VCL3pBTkJna3Foa2lHOXcwQkFRMEZBQU9CZ1FCeUZPbCtoTUZJQ2JkM0RKZm5wMlJnZC9kcXR0c1pHL3R5aElMV3ZFcmJpby9ERWU5OG1YcG93aFRrQzA0RU5wck95WGk3WmJVcWlpY0Y4OXVBR3l0MW9xZ1RVQ0QxVnNMYWhxSWNtcnpndW1OeVR3TEdXbzE3V0RBYTEvdXNEaGV0V0FNaGd6Ri9DbmY1ZWswbkswMG0wWVpHeWM0THpnRDBDUk9NQVNUV05nPT08L2RzOlg1MDlDZXJ0aWZpY2F0ZT48L2RzOlg1MDlEYXRhPjwvZHM6S2V5SW5mbz48L2RzOlNpZ25hdHVyZT4NCiAgICA8c2FtbDpTdWJqZWN0Pg0KICAgICAgPHNhbWw6TmFtZUlEIFNQTmFtZVF1YWxpZmllcj0iaHR0cDovL3NwLmV4YW1wbGUuY29tL2RlbW8xL21ldGFkYXRhLnBocCIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDp0cmFuc2llbnQiPl9jZTNkMjk0OGI0Y2YyMDE0NmRlZTBhMGIzZGQ2ZjY5YjZjZjg2ZjYyZDc8L3NhbWw6TmFtZUlEPg0KICAgICAgPHNhbWw6U3ViamVjdENvbmZpcm1hdGlvbiBNZXRob2Q9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpjbTpiZWFyZXIiPg0KICAgICAgICA8c2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uRGF0YSBOb3RPbk9yQWZ0ZXI9IjIwMTYtMDctMTRUMDY6MjE6NDhaIiBSZWNpcGllbnQ9Imh0dHA6Ly9zcC5leGFtcGxlLmNvbS9kZW1vMS9pbmRleC5waHA/YWNzIiBJblJlc3BvbnNlVG89Ik9ORUxPR0lOXzRmZWUzYjA0NjM5NWM0ZTc1MTAxMWU5N2Y4OTAwYjUyNzNkNTY2ODUiLz4NCiAgICAgIDwvc2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uPg0KICAgIDwvc2FtbDpTdWJqZWN0Pg0KICAgIDxzYW1sOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE0LTA3LTE3VDAxOjAxOjE4WiIgTm90T25PckFmdGVyPSIyMDE2LTA3LTE0VDA2OjIxOjQ4WiI+DQogICAgICA8c2FtbDpBdWRpZW5jZVJlc3RyaWN0aW9uPg0KICAgICAgICA8c2FtbDpBdWRpZW5jZT5odHRwOi8vc3AuZXhhbXBsZS5jb20vZGVtbzEvbWV0YWRhdGEucGhwPC9zYW1sOkF1ZGllbmNlPg0KICAgICAgPC9zYW1sOkF1ZGllbmNlUmVzdHJpY3Rpb24+DQogICAgPC9zYW1sOkNvbmRpdGlvbnM+DQogICAgPHNhbWw6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0YW50PSIyMDE0LTA3LTE3VDAxOjAxOjQ4WiIgU2Vzc2lvbk5vdE9uT3JBZnRlcj0iMjAyNC0wNy0xN1QwOTowMTo0OFoiIFNlc3Npb25JbmRleD0iX2JlOTk2N2FiZDkwNGRkY2FlM2MwZWI0MTg5YWRiZTNmNzFlMzI3Y2Y5MyI+DQogICAgICA8c2FtbDpBdXRobkNvbnRleHQ+DQogICAgICAgIDxzYW1sOkF1dGhuQ29udGV4dENsYXNzUmVmPnVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkPC9zYW1sOkF1dGhuQ29udGV4dENsYXNzUmVmPg0KICAgICAgPC9zYW1sOkF1dGhuQ29udGV4dD4NCiAgICA8L3NhbWw6QXV0aG5TdGF0ZW1lbnQ+DQogICAgPHNhbWw6QXR0cmlidXRlU3RhdGVtZW50Pg0KICAgICAgPHNhbWw6QXR0cmlidXRlIE5hbWU9InVpZCIgTmFtZUZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmF0dHJuYW1lLWZvcm1hdDpiYXNpYyI+DQogICAgICAgIDxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhzaTp0eXBlPSJ4czpzdHJpbmciPnRlc3Q8L3NhbWw6QXR0cmlidXRlVmFsdWU+DQogICAgICA8L3NhbWw6QXR0cmlidXRlPg0KICAgICAgPHNhbWw6QXR0cmlidXRlIE5hbWU9Im1haWwiIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6YmFzaWMiPg0KICAgICAgICA8c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj50ZXN0QGV4YW1wbGUuY29tPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPg0KICAgICAgPC9zYW1sOkF0dHJpYnV0ZT4NCiAgICAgIDxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJlZHVQZXJzb25BZmZpbGlhdGlvbiIgTmFtZUZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmF0dHJuYW1lLWZvcm1hdDpiYXNpYyI+DQogICAgICAgIDxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhzaTp0eXBlPSJ4czpzdHJpbmciPnVzZXJzPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPg0KICAgICAgICA8c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj5leGFtcGxlcm9sZTE8L3NhbWw6QXR0cmlidXRlVmFsdWU+DQogICAgICA8L3NhbWw6QXR0cmlidXRlPg0KICAgIDwvc2FtbDpBdHRyaWJ1dGVTdGF0ZW1lbnQ+DQogIDwvc2FtbDpBc3NlcnRpb24+DQo8L3NhbWxwOlJlc3BvbnNlPg==";

		InitializationService.initialize();

		Decoder decoder = Base64.getDecoder();
		byte[] decodedSamlBytes = decoder.decode(base64EncodedSAML);
		System.out.println("DECODED:" + new String(decodedSamlBytes));

		InputStream is = new ByteArrayInputStream(decodedSamlBytes);
		BasicParserPool pp = new BasicParserPool();
		pp.initialize();
		Document samlDoc = pp.parse(is);
		Element messageElem = samlDoc.getDocumentElement();		
		Unmarshaller unmarshaller = XMLObjectSupport.getUnmarshaller(messageElem);

	    Response response = (Response) unmarshaller.unmarshall(messageElem);
	    
		List<Assertion> assestions = response.getAssertions();
		System.out.println("How  many asserstions ?" + assestions.size());
		
		Assertion assertion = assestions.get(0);
       
		//AGE VALIDATION
		
		System.out.println("assertion.getConditions().getNotOnOrAfter()"+assertion.getConditions().getNotOnOrAfter());
        
        if (assertion.getConditions().getNotBefore() != null && assertion.getConditions().getNotBefore().isAfterNow()) {
            throw new ValidationException("Condition states that assertion is not yet valid (is the server time correct?)");
        }

        if (assertion.getConditions().getNotOnOrAfter() != null
                        && (assertion.getConditions().getNotOnOrAfter().isBeforeNow() || assertion.getConditions().getNotOnOrAfter().isEqualNow())) {
            throw new ValidationException("Condition states that assertion is no longer valid (is the server time correct?)");
        }
        
                
      //SIGNATURE VERIFICATION 
        System.out.println("Public key to verify signature ?"+assestions.get(0).getSignature().getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0));
        
        final org.opensaml.xmlsec.signature.X509Certificate certInSaml = assertion.getSignature().getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0);
                
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream inStream = new ByteArrayInputStream(decoder.decode(certInSaml.getValue().getBytes()));
        X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
        
        BasicX509Credential publicCredential = new BasicX509Credential(cert); 
        
        try {
			SignatureValidator.validate(assertion.getSignature(),publicCredential );
		} catch (SignatureException e) {
			System.out.println("ALERT ALERT ALERT SIGNATURE VALIDATION FAILED");
			throw e;
		}
         
        System.out.println("YAY!!! Iam here with no exception so signature is valid");
        
        //ATTRIBUTE EXTRACTION
         assertion.getAttributeStatements().get(0).getAttributes().forEach(attribute -> {System.out.println(attribute.getName()+"::"+attribute.getAttributeValues().get(0).getDOM().getTextContent());});
	}

}
