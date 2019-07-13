using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IotDataStore.Models.RequestBodies
{
    public class LoginCredentials
    {
        public string username { get; set; }
        public string password { get; set; }
    }
}
