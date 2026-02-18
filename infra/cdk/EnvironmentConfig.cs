using Amazon.CDK;

namespace ShumelaHire.Infra;

public sealed class EnvironmentConfig
{
    public required string EnvironmentName { get; init; }
    public required string DomainName { get; init; }
    public required string ApiDomainName { get; init; }
    public string? CertificateArn { get; init; }
    public string? ApiCertificateArn { get; init; }
    public required string Region { get; init; }

    public bool IsProduction => EnvironmentName == "prod";

    public string[] CorsOrigins => EnvironmentName switch
    {
        "prod" => new[] { $"https://{DomainName}", $"https://www.{DomainName}" },
        "ppe" => new[] { $"https://ppe.{DomainName}" },
        "sbx" => new[] { $"https://sbx.{DomainName}" },
        "dev" => new[] { $"https://dev.{DomainName}", "http://localhost:3000" },
        _ => new[] { "http://localhost:3000", "http://localhost:3001" }
    };

    public string UiUrl => EnvironmentName switch
    {
        "prod" => $"https://{DomainName}",
        _ => $"https://{EnvironmentName}.{DomainName}"
    };

    public string ApiUrl => EnvironmentName switch
    {
        "prod" => $"https://{ApiDomainName}",
        _ => $"https://api.{EnvironmentName}.{DomainName}"
    };

    public string Prefix => $"shumelahire-{EnvironmentName}";

    public static EnvironmentConfig FromContext(App app)
    {
        var env = (string?)app.Node.TryGetContext("env")
                  ?? System.Environment.GetEnvironmentVariable("SHUMELAHIRE_ENV")
                  ?? "dev";

        var domain = (string?)app.Node.TryGetContext("domain")
                     ?? System.Environment.GetEnvironmentVariable("SHUMELAHIRE_DOMAIN")
                     ?? "shumelahire.co.za";

        var region = (string?)app.Node.TryGetContext("region")
                     ?? System.Environment.GetEnvironmentVariable("CDK_DEFAULT_REGION")
                     ?? "af-south-1";

        var certArn = (string?)app.Node.TryGetContext("certificateArn")
                      ?? System.Environment.GetEnvironmentVariable("CERTIFICATE_ARN");

        var apiCertArn = (string?)app.Node.TryGetContext("apiCertificateArn")
                         ?? System.Environment.GetEnvironmentVariable("API_CERTIFICATE_ARN");

        return new EnvironmentConfig
        {
            EnvironmentName = env,
            DomainName = domain,
            ApiDomainName = $"api.{domain}",
            CertificateArn = certArn,
            ApiCertificateArn = apiCertArn,
            Region = region
        };
    }
}
