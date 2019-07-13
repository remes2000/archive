using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(ZapTS.Web.Startup))]
namespace ZapTS.Web
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            
        }
    }
}
