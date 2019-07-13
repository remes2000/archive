using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using IotDataStore.Models;
using IotDataStore.Models.RequestBodies;
using System.Reflection;
using System.Dynamic;
using Newtonsoft.Json;
using System.ComponentModel;
using Dynamitey;

namespace IotDataStore.Controllers
{
    [Produces("application/json")]
    [Route("api/Measurements")]
    public class MeasurementsController : Controller
    {
        private readonly IotDataStoreContext _context;

        public MeasurementsController(IotDataStoreContext context)
        {
            _context = context;
        }

        // GET: api/Measurements
        [HttpGet]
        public IEnumerable<Measurement> GetMeasurements()
        {
            return _context.Measurements;
        }

        [HttpGet]
        [Route("/api/Measurements/Table/{id}")]
        public IActionResult GetMeasurementsByTableId(int id)
        {
            var measurements = _context.Measurements.Where(m => m.TableId == id).ToArray();

            if (measurements.Length == 0)
                return NotFound();

            return Ok(measurements);
        }

        // POST: api/Measurements
        [HttpPost]
        public IActionResult PostMeasurement([FromBody] NewMeasurement newMeasurement)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest();
            }

            Project project = _context.Projects.FirstOrDefault(p => p.Id == newMeasurement.ProjectId);
            if (project == null)
                return NotFound();

            if (project.SecretKey != newMeasurement.SecretKey)
                return Unauthorized();

            Table table = _context.Tables.FirstOrDefault(t => t.Id == newMeasurement.TableId);
            if (table == null)
                return NotFound();

            dynamic modelObject = JsonConvert.DeserializeObject<dynamic>(table.TableItemModel);

            if (!IsObjectValid(modelObject, newMeasurement.Data))
                return BadRequest();

            Measurement measurement = new Measurement
            {
                TableId = table.Id,
                Data = JsonConvert.SerializeObject(newMeasurement.Data)
            };

            _context.Measurements.Add(measurement);
            _context.SaveChanges();

            return Ok();
        }

        private bool IsObjectValid(dynamic modelObject, dynamic testObject)
        {

            IEnumerable<string> testObjectMembers = Dynamic.GetMemberNames(testObject, true);
            IEnumerable<string> modelObjectMembers = Dynamic.GetMemberNames(modelObject, true);

            foreach (string propertyName in testObjectMembers)
            {
                if (!modelObjectMembers.Contains(propertyName))
                    return false;
            }

            return true;
        }
    }
}