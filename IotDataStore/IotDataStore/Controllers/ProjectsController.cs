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
using System.Security.Claims;
using System.IdentityModel.Tokens.Jwt;

namespace IotDataStore.Controllers
{
    [Produces("application/json")]
    [Route("api/Projects")]
    public class ProjectsController : Controller
    {
        private readonly IotDataStoreContext _context;

        public ProjectsController(IotDataStoreContext context)
        {
            _context = context;
        }

        // GET: api/Projects
        [HttpGet]
        public IEnumerable<Project> GetProjects()
        {
            return _context.Projects;
        }

        [HttpGet]
        [Route("/api/Projects/{id}")]
        public IActionResult GetProject(int id)
        {
            var project = _context.Projects.FirstOrDefault(p => p.Id == id);
            if (project != null)
                return Ok(new { Id = project.Id, UserId = project.UserId, Title = project.Title, Description = project.Description});
            else
                return NotFound();
        }

        [HttpGet]
        [Authorize]
        [Route("/api/Projects/{id}/SecretKey")]
        public IActionResult GetProjectSecretKey(int id)
        {
            Project project = _context.Projects.FirstOrDefault(p => p.Id == id);
            if (project == null)
                return NotFound();
            //Check if Current User is project owner
            int userId = Convert.ToInt32(User.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Jti).Value);
            if (project.UserId == userId)
                return Ok(project.SecretKey);

            return Unauthorized();
        }

        [HttpGet]
        [Route("/api/Projects/Owner/{id}")]
        public IActionResult GetProjectsByOwnerId(int id)
        {
            var projects = _context.Projects
                .Where(p => p.UserId == id)
                .Select(p => new { title = p.Title, description = p.Description, id = p.Id })
                .ToArray();
            if (projects.Length == 0)
                return NotFound();
            return Ok(projects);
        }

        // POST: api/Projects
        [HttpPost]
        [Authorize]
        public IActionResult PostProject([FromBody] NewProject newProject)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest();
            }

            int userId = Convert.ToInt32(User.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Jti).Value);

            Project project = new Project
            {
                Title = newProject.Title,
                Description = newProject.Description,
                UserId = userId,
                SecretKey = GenerateSecretKey()
            };

            _context.Projects.Add(project);
            _context.SaveChanges();

            return Ok(new { id = project.Id});
        }

        private string GenerateSecretKey()
        {
            Random random = new Random();
            const int length = 25;
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+<>{}";

            return new string(Enumerable.Repeat(chars, length)
                .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }
}