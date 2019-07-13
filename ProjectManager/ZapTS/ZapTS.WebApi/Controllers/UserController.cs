using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using ZapTS.WebApi.Models;
using System.Web.Http.Cors;
using ZapTS.WebApi.Annotations;
using Scrypt;

namespace ZapTS.WebApi.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class UserController : ApiController
    {
        [HttpPost]
        public HttpResponseMessage Login(LoginViewModel model)
        {
            using ( var db = new ProjectManagerEntities())
            {
                ScryptEncoder encoder = new ScryptEncoder();
                var user = db.Users.FirstOrDefault(q => q.Username == model.Username);

                if (user != null && encoder.Compare(model.Password, user.Password))
                {
                    String SessionId = Guid.NewGuid().ToString();
                    db.Sessions.Add(new Sessions() { SessionId = SessionId, UserId = user.Id });
                    db.SaveChanges();

                    return Request.CreateResponse(HttpStatusCode.OK, SessionId);
                }

                var message = string.Format("Wrong password/username");
                HttpError err = new HttpError(message);
                return Request.CreateResponse(HttpStatusCode.NotFound, err);
            }
        }

        [HttpPost]
        public HttpResponseMessage Register(RegisterViewModel model)
        {
            using (var db = new ProjectManagerEntities())
            {
                var users = db.Users;
                var existingUser = users.Any(q => q.Username == model.Username || q.Email == model.Email);
                if (!existingUser) {

                    ScryptEncoder encoder = new ScryptEncoder();

                    var newUser = new Users()
                    {
                        Username = model.Username,
                        Name = model.Name,
                        Surname = model.Surname,
                        Email = model.Email,
                        Password = encoder.Encode(model.Password)
                    };

                    users.Add(newUser);
                    db.SaveChanges();
                    return Request.CreateResponse(HttpStatusCode.OK, true);
                }
            }

            var message = string.Format("This Username/Email is not avaliable!");
            HttpError err = new HttpError(message);
            return Request.CreateResponse(HttpStatusCode.NotFound, err);
        }

        [Authorization]
        public HttpResponseMessage GetImportantData()
        {
            return Request.CreateResponse(HttpStatusCode.OK, true);
        }

    }
}
