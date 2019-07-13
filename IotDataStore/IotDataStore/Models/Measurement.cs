using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IotDataStore.Models
{
    public class Measurement
    {
        public int Id { get; set; }
        public int TableId { get; set; }
        public string Data { get; set; }
    }
}
