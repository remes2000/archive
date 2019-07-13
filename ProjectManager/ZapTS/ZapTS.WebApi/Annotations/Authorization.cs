using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;
using System.Web.Http.Controllers;
using System.Net.Http;
using System.Web.Http.Filters;
using System.Web.Http.ModelBinding;
using System.Net;
using System.Web.Http;

namespace ZapTS.WebApi.Annotations
{
    public class AuthorizationAttribute : ActionFilterAttribute
    {

        public override void OnActionExecuting(HttpActionContext actionContext)
        {
            try
            {
                string sessionId = actionContext.Request.Headers.GetValues("SessionId").First();

                using (var db = new ProjectManagerEntities())
                {
                    var session = db.Sessions.FirstOrDefault(q => q.SessionId == sessionId);

                    if (session == null)
                    {
                        var message = string.Format("No SessionId Header");
                        HttpError err = new HttpError(message);
                        actionContext.Response = actionContext.Request.CreateResponse(HttpStatusCode.BadRequest, err);
                    }
                }

            } catch(Exception error)
            {
                var message = string.Format("No SessionId Header");
                HttpError err = new HttpError(message);
                actionContext.Response = actionContext.Request.CreateResponse(HttpStatusCode.BadRequest, err);
            }
        }
    }
}


