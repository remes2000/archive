using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IotDataStore.Models
{
    public class Table
    {
        public int Id { get; set; }
        public int ProjectId { get; set; }
        public string Title { get; set; }
        public string TableItemModel { get; set; }
    }


}
