using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IotDataStore.Models.RequestBodies
{
    public class NewProject
    {
        public string Title { get; set; }
        public string Description { get; set; }
    }
}
