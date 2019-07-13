using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using IotDataStore.Models;
using IotDataStore.Models.RequestBodies;
using Microsoft.AspNetCore.Authorization;
using System.IdentityModel.Tokens.Jwt;

namespace IotDataStore.Controllers
{
    [Produces("application/json")]
    [Route("api/Tables")]
    public class TablesController : Controller
    {
        private readonly IotDataStoreContext _context;

        public TablesController(IotDataStoreContext context)
        {
            _context = context;
        }

        // GET: api/Tables
        [HttpGet]
        public IEnumerable<Table> GetTables()
        {
            return _context.Tables;
        }

        [HttpGet]
        [Route("/api/Tables/{tableId}")]
        public IActionResult GetTableById(int tableId)
        {
            Table table = _context.Tables.SingleOrDefault(t => t.Id == tableId);
            if (table == null)
                return NotFound();
            return Ok(table);
        }

        [HttpGet]
        [Route("/api/Tables/Project/{projectId}")]
        public IActionResult GetProjectsTables(int projectId)
        {
            var tables = _context.Tables
                .Where(t => t.ProjectId == projectId)
                .ToArray();

            if (tables.Length == 0)
                return NotFound();

            return Ok(tables);
        }

        // POST: api/Tables
        [Authorize]
        [HttpPost]
        public IActionResult PostTable([FromBody] NewTable requestTable)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest();
            }

            int userId = Convert.ToInt32(User.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Jti).Value);

            Project project = _context.Projects.FirstOrDefault(p => p.Id == requestTable.ProjectId);

            if (project == null)
                return NotFound();

            if (project.UserId != userId)
                return Unauthorized();

            Table newTable = new Table
            {
                Title = requestTable.Title,
                ProjectId = requestTable.ProjectId,
                TableItemModel = requestTable.TableItemModel
            };

            _context.Tables.Add(newTable);
            _context.SaveChanges();

            return Ok(newTable.Id);
        }
    }
}