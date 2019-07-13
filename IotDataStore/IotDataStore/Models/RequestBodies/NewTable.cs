using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IotDataStore.Models.RequestBodies
{
    public class NewTable
    {
        public string Title { get; set; }
        public int ProjectId { get; set; }
        public string TableItemModel { get; set; }
    }
}
