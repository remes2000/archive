using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IotDataStore.Models.RequestBodies
{
    public class NewMeasurement
    {
        public string SecretKey { get; set; }
        public int ProjectId { get; set; }
        public int TableId { get; set; }

        public dynamic Data { get; set; }
    }
}
